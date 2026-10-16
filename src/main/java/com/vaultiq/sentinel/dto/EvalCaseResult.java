package com.vaultiq.sentinel.dto;

import java.util.List;

public record EvalCaseResult(
        String id,
        String question,
        boolean passed,
        String answer,
        List<String> missingKeywords,
        long latencyMs,
        String failure
) {
}
