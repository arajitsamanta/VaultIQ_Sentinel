package com.vaultiq.sentinel.dto;

import java.time.Instant;
import java.util.List;

public record EvalReport(
        Instant startedAt,
        long totalLatencyMs,
        int passed,
        int failed,
        List<EvalCaseResult> cases
) {
}
