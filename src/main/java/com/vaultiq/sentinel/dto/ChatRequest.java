package com.vaultiq.sentinel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "Question must not be blank")
        @Size(max = 10000, message = "Question must not exceed 10000 characters")
        String question
) {
}
