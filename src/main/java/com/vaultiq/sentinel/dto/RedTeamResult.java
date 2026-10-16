package com.vaultiq.sentinel.dto;

public record RedTeamResult(String id, boolean caught, String expectedGuardrail, String outcome) {
}
