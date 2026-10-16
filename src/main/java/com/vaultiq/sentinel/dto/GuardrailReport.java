package com.vaultiq.sentinel.dto;

public record GuardrailReport(
        int piiRedactions,
        int inputPiiRedactions,
        int outputPiiRedactions,
        String injectionScreen,
        double groundingScore,
        boolean grounded,
        double costUsd,
        long latencyMs,
        String model,
        boolean tokenCountsEstimated
) {
}
