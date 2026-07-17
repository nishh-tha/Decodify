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
 * 3. Parses the JSON text Gemini returns into an ExplainResponse.
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
                    "Gemini API key is not configured. Set the GEMINI_API_KEY environment variable.");
        }

        String prompt = buildPrompt(language, code);
        String rawJsonText = callGeminiApi(prompt);
        return parseExplanation(rawJsonText);
    }

    /**
     * Builds the instruction prompt sent to Gemini.
     * We explicitly ask for raw JSON only, so the backend can parse it directly.
     */
    private String buildPrompt(String language, String code) {
        return """
                You are a friendly programming tutor helping a beginner understand a piece of code.

                Analyze the following %s code and explain it.

                Respond with ONLY a raw JSON object (no markdown, no code fences, no extra text)
                using EXACTLY this structure:
                {
                  "summary": "A simple, beginner-friendly explanation of what the code does",
                  "timeComplexity": "The Big-O time complexity with a short reason",
                  "spaceComplexity": "The Big-O space complexity with a short reason",
                  "issues": ["A possible bug or edge case", "Another possible issue"],
                  "suggestions": ["A suggestion to improve readability or performance", "Another suggestion"]
                }

                Rules:
                - Keep every field concise and easy for a beginner to understand.
                - "issues" and "suggestions" must be JSON arrays of short strings (2-4 items each).
                - If there are no obvious issues, return an array with a single string saying so.
                - Do not wrap the JSON in markdown code fences.
                - Do not include any text before or after the JSON object.

                Code:
                ```%s
                %s
                ```
                """.formatted(language, language, code);
    }

    /**
     * Sends the prompt to the Gemini generateContent endpoint and
     * returns the raw text of the model's reply.
     */
    private String callGeminiApi(String prompt) {
        try {
            var requestBody = new java.util.HashMap<String, Object>();
            var content = new java.util.HashMap<String, Object>();
            var part = new java.util.HashMap<String, Object>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            JsonNode response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                throw new GeminiApiException("Gemini API returned an empty response");
            }

            return response
                    .path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text").asText();

        } catch (GeminiApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GeminiApiException("Failed to call Gemini API: " + ex.getMessage(), ex);
        }
    }

    /**
     * Parses the JSON text returned by Gemini into our ExplainResponse DTO.
     * Also strips markdown code fences in case the model adds them anyway.
     */
    private ExplainResponse parseExplanation(String rawText) {
        try {
            String cleaned = rawText.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceFirst("^```(json)?", "").trim();
                if (cleaned.endsWith("```")) {
                    cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
                }
            }

            JsonNode node = objectMapper.readTree(cleaned);

            String summary = node.path("summary").asText("No summary available.");
            String timeComplexity = node.path("timeComplexity").asText("Not specified.");
            String spaceComplexity = node.path("spaceComplexity").asText("Not specified.");
            List<String> issues = toStringList(node.path("issues"));
            List<String> suggestions = toStringList(node.path("suggestions"));

            return new ExplainResponse(summary, timeComplexity, spaceComplexity, issues, suggestions);

        } catch (Exception ex) {
            throw new GeminiApiException("Failed to parse AI response: " + ex.getMessage(), ex);
        }
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode.isArray()) {
            arrayNode.forEach(item -> result.add(item.asText()));
        }
        return result;
    }
}
