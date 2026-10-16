package com.vaultiq.sentinel.dto;

import com.vaultiq.sentinel.model.RequestMetric;

import java.util.List;

public record MetricsResponse(
        List<RequestMetric> requests,
        long totalInputTokens,
        long totalOutputTokens,
        double totalCostUsd,
        double dailyUsdCap,
        boolean dailyCapExceeded
) {
}
