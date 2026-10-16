package com.vaultiq.sentinel.controller;

import com.vaultiq.sentinel.config.SentinelProperties;
import com.vaultiq.sentinel.dto.GuardrailConfigResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel/guardrails")
public class GuardrailsController {

    private final SentinelProperties properties;

    public GuardrailsController(SentinelProperties properties) {
        this.properties = properties;
    }

    @GetMapping
    public GuardrailConfigResponse config() {
        return new GuardrailConfigResponse(
                properties.getPii().getRules().stream()
                        .map(rule -> new GuardrailConfigResponse.PiiRuleConfig(rule.getLabel(), rule.getPattern()))
                        .toList(),
                properties.getPii().isModelPass(),
                properties.getGrounding().getMinScore(),
                properties.getPricing().getInputPer1k(),
                properties.getPricing().getOutputPer1k(),
                properties.getBudget().getDailyUsdCap(),
                properties.getInjectionPatterns()
        );
    }
}
