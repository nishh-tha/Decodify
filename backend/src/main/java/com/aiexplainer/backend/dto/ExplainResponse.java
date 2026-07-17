package com.aiexplainer.backend.dto;

import java.util.List;

/**
 * Represents the clean JSON object returned to the frontend:
 * {
 *   "summary": "...",
 *   "timeComplexity": "...",
 *   "spaceComplexity": "...",
 *   "issues": ["...", "..."],
 *   "suggestions": ["...", "..."]
 * }
 */
public class ExplainResponse {

    private String summary;
    private String timeComplexity;
    private String spaceComplexity;
    private List<String> issues;
    private List<String> suggestions;

    public ExplainResponse() {
    }

    public ExplainResponse(String summary, String timeComplexity, String spaceComplexity,
                            List<String> issues, List<String> suggestions) {
        this.summary = summary;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.issues = issues;
        this.suggestions = suggestions;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
