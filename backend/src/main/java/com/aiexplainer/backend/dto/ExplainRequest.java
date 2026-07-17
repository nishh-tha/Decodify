package com.aiexplainer.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the JSON body sent by the frontend:
 * {
 *   "language": "Java",
 *   "code": "..."
 * }
 */
public class ExplainRequest {

    @NotBlank(message = "language must not be empty")
    private String language;

    @NotBlank(message = "code must not be empty")
    private String code;

    public ExplainRequest() {
    }

    public ExplainRequest(String language, String code) {
        this.language = language;
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
