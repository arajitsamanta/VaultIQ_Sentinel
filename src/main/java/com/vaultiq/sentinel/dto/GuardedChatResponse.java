package com.vaultiq.sentinel.dto;

import java.util.List;

public record GuardedChatResponse(
        String question,
        String answer,
        List<CitationDto> citations,
        GuardrailReport guardrails
) {
}
