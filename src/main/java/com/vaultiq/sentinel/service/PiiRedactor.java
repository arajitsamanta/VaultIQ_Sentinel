package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.config.SentinelProperties;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PiiRedactor {

    private final SentinelProperties properties;

    public PiiRedactor(SentinelProperties properties) {
        this.properties = properties;
    }

    public RedactionResult redact(String text) {
        String redacted = text;
        int count = 0;
        for (SentinelProperties.Rule rule : properties.getPii().getRules()) {
            Pattern pattern = Pattern.compile(rule.getPattern(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(redacted);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                if ("CREDIT_CARD".equals(rule.getLabel()) && !passesLuhn(matcher.group())) {
                    matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
                    continue;
                }
                count++;
                matcher.appendReplacement(result,
                        Matcher.quoteReplacement("[REDACTED:" + rule.getLabel() + "]"));
            }
            matcher.appendTail(result);
            redacted = result.toString();
        }
        return new RedactionResult(redacted, count);
    }

    private boolean passesLuhn(String value) {
        String digits = value.replaceAll("\\D", "");
        if (digits.length() < 13 || digits.length() > 16) {
            return false;
        }
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int digit = digits.charAt(i) - '0';
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}
