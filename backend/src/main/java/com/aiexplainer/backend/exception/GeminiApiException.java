package com.aiexplainer.backend.exception;

/**
 * Thrown whenever something goes wrong while calling the Gemini API
 * or while parsing its response.
 */
public class GeminiApiException extends RuntimeException {

    public GeminiApiException(String message) {
        super(message);
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
