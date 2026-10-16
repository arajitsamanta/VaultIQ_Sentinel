package com.vaultiq.sentinel.dto;

public record CitationDto(
        String documentId,
        String documentName,
        int chunkIndex,
        String text,
        Double relevanceScore
) {
}
