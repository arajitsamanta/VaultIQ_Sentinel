package com.vaultiq.sentinel.dto;

import java.util.List;

public record VaultIqChatResponse(String question, String answer, List<CitationDto> citations) {
}
