package com.aiexplainer.backend.service;

import com.aiexplainer.backend.dto.ExplainResponse;
import com.aiexplainer.backend.exception.GeminiApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all communication with the Google Gemini API:
 * 1. Builds a beginner-friendly prompt.
 * 2. Sends it to Gemini.
 * 3. Requests structured JSON from Gemini.
 * 4. Parses the JSON into an ExplainResponse.
 */
@Service
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ExplainResponse explainCode(String language, String code) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new GeminiApiException(
                    "Gemini API key is not configured. Set the GEMINI_API_KEY environment variable."
            );
        }

        String prompt = buildPrompt(language, code);
        String rawJsonText = callGeminiApi(prompt);

        return parseExplanation(rawJsonText);
    }

    /**
     * Builds the instruction prompt sent to Gemini.
     */
    private String buildPrompt(String language, String code) {
        return """
                You are a beginner-friendly programming tutor.

                Analyze this %s code.

                Provide:
                - A concise explanation of what the code does.
                - Time complexity with a short reason.
                - Space complexity with a short reason.
                - 1-3 possible bugs or edge cases.
                - 1-3 suggestions for improving readability or performance.

                Keep every field concise and beginner-friendly.
                Only report issues that are actually relevant to the provided code.
                Analyze edge cases based on the actual execution order of the code.
                Do not report an error that cannot be reached because an earlier statement would fail first.
                Avoid reporting multiple issues caused by the same underlying condition.
                If there are no obvious issues, return "No obvious issues found." in the issues array.

                Code:
                %s
                """.formatted(language, code);
    }

    /**
     * Sends the prompt to Gemini and requests structured JSON.
     */
    private String callGeminiApi(String prompt) {
        try {
            var requestBody = new java.util.HashMap<String, Object>();

            // Build the content
            var content = new java.util.HashMap<String, Object>();
            var part = new java.util.HashMap<String, Object>();

            part.put("text", prompt);
            content.put("parts", List.of(part));

            requestBody.put("contents", List.of(content));

            // Generation configuration
            var generationConfig = new java.util.HashMap<String, Object>();

            generationConfig.put("maxOutputTokens", 2048);
            generationConfig.put("responseMimeType", "application/json");

            // Keep reasoning lightweight for faster responses
            var thinkingConfig = new java.util.HashMap<String, Object>();
            thinkingConfig.put("thinkingLevel", "low");

            generationConfig.put("thinkingConfig", thinkingConfig);

            // Define the exact JSON structure Decodify expects
            var responseSchema = new java.util.HashMap<String, Object>();

            responseSchema.put("type", "OBJECT");

            var properties = new java.util.HashMap<String, Object>();

            // summary
            var summarySchema = new java.util.HashMap<String, Object>();
            summarySchema.put("type", "STRING");
            properties.put("summary", summarySchema);

            // timeComplexity
            var timeComplexitySchema = new java.util.HashMap<String, Object>();
            timeComplexitySchema.put("type", "STRING");
            properties.put("timeComplexity", timeComplexitySchema);

            // spaceComplexity
            var spaceComplexitySchema = new java.util.HashMap<String, Object>();
            spaceComplexitySchema.put("type", "STRING");
            properties.put("spaceComplexity", spaceComplexitySchema);

            // issues
            var issuesSchema = new java.util.HashMap<String, Object>();
            issuesSchema.put("type", "ARRAY");

            var issueItems = new java.util.HashMap<String, Object>();
            issueItems.put("type", "STRING");

            issuesSchema.put("items", issueItems);
            properties.put("issues", issuesSchema);

            // suggestions
            var suggestionsSchema = new java.util.HashMap<String, Object>();
            suggestionsSchema.put("type", "ARRAY");

            var suggestionItems = new java.util.HashMap<String, Object>();
            suggestionItems.put("type", "STRING");

            suggestionsSchema.put("items", suggestionItems);
            properties.put("suggestions", suggestionsSchema);

            // Attach properties to schema
            responseSchema.put("properties", properties);

            // Require every field
            responseSchema.put(
                    "required",
                    List.of(
                            "summary",
                            "timeComplexity",
                            "spaceComplexity",
                            "issues",
                            "suggestions"
                    )
            );

            // Attach schema to generation config
            generationConfig.put("responseSchema", responseSchema);

            // Attach generation config to request
            requestBody.put("generationConfig", generationConfig);

            // Call Gemini
            JsonNode response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                throw new GeminiApiException(
                        "Gemini API returned an empty response"
                );
            }

            // Extract Gemini's generated text
            String result = response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            if (result == null || result.isBlank()) {
                throw new GeminiApiException(
                        "Gemini API returned an empty explanation"
                );
            }

            return result;

        } catch (GeminiApiException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new GeminiApiException(
                    "Failed to call Gemini API: " + ex.getMessage(),
                    ex
            );
        }
    }

    /**
     * Parses Gemini's JSON response into ExplainResponse.
     */
    private ExplainResponse parseExplanation(String rawText) {
        try {
            String cleaned = rawText.trim();

            // Remove markdown code fences if Gemini happens to return them
            if (cleaned.startsWith("```")) {
                cleaned = cleaned
                        .replaceFirst("^```(json)?", "")
                        .trim();

                if (cleaned.endsWith("```")) {
                    cleaned = cleaned
                            .substring(0, cleaned.length() - 3)
                            .trim();
                }
            }

            JsonNode node = objectMapper.readTree(cleaned);

            String summary = node
                    .path("summary")
                    .asText("No summary available.");

            String timeComplexity = node
                    .path("timeComplexity")
                    .asText("Not specified.");

            String spaceComplexity = node
                    .path("spaceComplexity")
                    .asText("Not specified.");

            List<String> issues =
                    toStringList(node.path("issues"));

            List<String> suggestions =
                    toStringList(node.path("suggestions"));

            return new ExplainResponse(
                    summary,
                    timeComplexity,
                    spaceComplexity,
                    issues,
                    suggestions
            );

        } catch (Exception ex) {
            throw new GeminiApiException(
                    "Failed to parse AI response: " + ex.getMessage(),
                    ex
            );
        }
    }

    /**
     * Converts a JSON array into a Java List<String>.
     */
    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();

        if (arrayNode.isArray()) {
            arrayNode.forEach(item ->
                    result.add(item.asText())
            );
        }

        return result;
    }
}