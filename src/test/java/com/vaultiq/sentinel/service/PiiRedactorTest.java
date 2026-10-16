package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.config.SentinelProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PiiRedactorTest {

    private PiiRedactor redactor() {
        SentinelProperties properties = new SentinelProperties();
        properties.getPii().setRules(List.of(
                new SentinelProperties.Rule("SSN", "(?<!\\d)\\d{3}-\\d{2}-\\d{4}(?!\\d)"),
                new SentinelProperties.Rule("CREDIT_CARD", "(?<!\\d)(?:\\d[ -]*?){13,16}(?!\\d)"),
                new SentinelProperties.Rule("EMAIL", "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b"),
                new SentinelProperties.Rule("PHONE", "(?<![A-Z0-9])(?:\\+?1[ -]?)?(?:\\(\\d{3}\\)|\\d{3})[ -.]?\\d{3}[ -.]?\\d{4}(?![A-Z0-9])"),
                new SentinelProperties.Rule("API_KEY", "\\bsk-[A-Za-z0-9_-]{16,}\\b"),
                new SentinelProperties.Rule("API_KEY", "\\bsk-[A-Za-z0-9_-]{16,}\\b")
        ));
        return new PiiRedactor(properties);
    }

    @Test
    void redactsSupportedPiiAndCountsMatches() {
        RedactionResult result = redactor().redact(
                "SSN 123-45-6789, email jane@example.com, phone 415-555-2671, "
                        + "card 4111 1111 1111 1111, key sk-1234567890abcdef");

        assertThat(result.text()).contains("[REDACTED:SSN]", "[REDACTED:EMAIL]", "[REDACTED:PHONE]",
                "[REDACTED:CREDIT_CARD]", "[REDACTED:API_KEY]");
        assertThat(result.redactions()).isEqualTo(5);
    }

    @Test
    void leavesTextWithoutPiiUntouchedAndRejectsInvalidCardChecksum() {
        String text = "Ignore the previous paragraph and summarize. Card 4111 1111 1111 1112";

        RedactionResult result = redactor().redact(text);

        assertThat(result.text()).isEqualTo(text);
        assertThat(result.redactions()).isZero();
    }
}
