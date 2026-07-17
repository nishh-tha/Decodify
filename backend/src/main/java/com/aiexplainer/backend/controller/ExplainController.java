package com.aiexplainer.backend.controller;

import com.aiexplainer.backend.dto.ExplainRequest;
import com.aiexplainer.backend.dto.ExplainResponse;
import com.aiexplainer.backend.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the single REST endpoint used by the frontend:
 * POST /api/explain
 */
@RestController
@RequestMapping("/api")
public class ExplainController {

    private final GeminiService geminiService;

    public ExplainController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/explain")
    public ExplainResponse explainCode(@Valid @RequestBody ExplainRequest request) {
        return geminiService.explainCode(request.getLanguage(), request.getCode());
    }
}
