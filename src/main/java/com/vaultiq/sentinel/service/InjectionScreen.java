package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.config.SentinelProperties;
import com.vaultiq.sentinel.dto.InjectionBlockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class InjectionScreen {

    private static final Logger log = LoggerFactory.getLogger(InjectionScreen.class);
    private static final List<Pattern> DEFAULT_PATTERNS = List.of(
            Pattern.compile("ignore\\s+(?:all\\s+)?previous\\s+instructions", Pattern.CASE_INSENSITIVE),
            Pattern.compile("reveal\\s+(?:your|the)\\s+system\\s+prompt", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\byou\\s+are\\s+now\\b", Pattern.CASE_INSENSITIVE)
    );
    private static final Pattern BASE64_CANDIDATE = Pattern.compile("(?<![A-Za-z0-9+/])[A-Za-z0-9+/]{20,}={0,2}(?![A-Za-z0-9+/])");

    private final List<Pattern> configuredPatterns;

    public InjectionScreen(SentinelProperties properties) {
        this.configuredPatterns = properties.getInjectionPatterns().stream()
                .map(pattern -> Pattern.compile(pattern, Pattern.CASE_INSENSITIVE))
                .toList();
    }

    public void check(String question) {
        if (matches(question) || hasEncodedOverride(question)) {
            log.warn("Blocked a prompt matching an injection pattern");
            throw new InjectionBlockedException("injection-attempt");
        }
    }

    private boolean matches(String value) {
        return DEFAULT_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(value).find())
                || configuredPatterns.stream().anyMatch(pattern -> pattern.matcher(value).find());
    }

    private boolean hasEncodedOverride(String value) {
        var matcher = BASE64_CANDIDATE.matcher(value);
        while (matcher.find()) {
            try {
                String decoded = new String(Base64.getDecoder().decode(matcher.group()), StandardCharsets.UTF_8);
                if (matches(decoded)) {
                    return true;
                }
            } catch (IllegalArgumentException ignored) {
                // Candidate text that is not valid Base64 is not an encoded payload.
            }
        }
        return false;
    }
}
