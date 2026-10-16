package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.dto.GuardrailReport;
import com.vaultiq.sentinel.dto.GuardedChatResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegressionRunnerTest {

    @Test
    void recordsPerCaseResultsAndLatestReport() {
        GuardedChat chatService = question -> new GuardedChatResponse(
                question, "The policy includes paid time off.", List.of(),
                new GuardrailReport(0, 0, 0, "clean", 1, true, 0, 1, "test", true));

        RegressionRunner runner = new RegressionRunner(chatService);
        var report = runner.run();

        assertThat(report.cases()).hasSize(7);
        assertThat(report.passed()).isGreaterThanOrEqualTo(1);
        assertThat(runner.latest()).isEqualTo(report);
    }
}
