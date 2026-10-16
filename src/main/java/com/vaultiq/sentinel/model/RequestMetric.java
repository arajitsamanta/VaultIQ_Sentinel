package com.vaultiq.sentinel.model;

import java.time.Instant;

public record RequestMetric(
        Instant timestamp,
        String model,
        long inputTokens,
        long outputTokens,
        double costUsd,
        long latencyMs,
        boolean tokenCountsEstimated
) {
}
