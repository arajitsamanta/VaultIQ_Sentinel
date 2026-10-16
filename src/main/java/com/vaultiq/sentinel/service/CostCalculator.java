package com.vaultiq.sentinel.service;

import org.springframework.stereotype.Component;

@Component
public class CostCalculator {

    public double calculate(long inputTokens, long outputTokens, double inputPer1k, double outputPer1k) {
        return (inputTokens / 1000.0 * inputPer1k) + (outputTokens / 1000.0 * outputPer1k);
    }

    public long estimateTokens(String text) {
        return text == null || text.isBlank() ? 0 : Math.max(1, (text.length() + 3L) / 4L);
    }
}
