package com.vaultiq.sentinel.dto;

import java.util.List;

public record GuardrailConfigResponse(
        List<PiiRuleConfig> piiRules,
        boolean piiModelPassEnabled,
        double minGroundingScore,
        double inputPricePer1k,
        double outputPricePer1k,
        double dailyUsdCap,
        List<String> injectionPatterns
) {
    public record PiiRuleConfig(String label, String pattern) {
    }
}
