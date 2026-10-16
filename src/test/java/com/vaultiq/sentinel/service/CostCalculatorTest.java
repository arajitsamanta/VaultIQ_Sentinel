package com.vaultiq.sentinel.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CostCalculatorTest {

    private final CostCalculator calculator = new CostCalculator();

    @Test
    void calculatesInputAndOutputPricing() {
        assertThat(calculator.calculate(1000, 500, 0.002, 0.004)).isEqualTo(0.004);
    }

    @Test
    void estimatesTokensAndSupportsZeroCostLocalModels() {
        assertThat(calculator.estimateTokens("12345678")).isEqualTo(2);
        assertThat(calculator.calculate(200, 100, 0, 0)).isZero();
    }
}
