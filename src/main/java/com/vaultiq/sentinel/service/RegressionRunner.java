package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.dto.EvalCaseResult;
import com.vaultiq.sentinel.dto.EvalReport;
import com.vaultiq.sentinel.dto.GuardedChatResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
public class RegressionRunner {

    private final GuardedChat chatService;
    private final AtomicReference<EvalReport> latestReport = new AtomicReference<>();

    public RegressionRunner(GuardedChat chatService) {
        this.chatService = chatService;
    }

    public EvalReport run() {
        Instant startedAt = Instant.now();
        long started = System.nanoTime();
        List<EvalCaseResult> results = new ArrayList<>();
        for (GoldenCase goldenCase : loadCases()) {
            long caseStarted = System.nanoTime();
            try {
                GuardedChatResponse response = chatService.chat(goldenCase.question());
                long latencyMs = (System.nanoTime() - caseStarted) / 1_000_000;
                List<String> missing = goldenCase.expectedKeywords().stream()
                        .filter(keyword -> !Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE)
                                .matcher(response.answer()).find())
                        .toList();
                int matchedKeywords = goldenCase.expectedKeywords().size() - missing.size();
                boolean passed = matchedKeywords >= goldenCase.minKeywords()
                        && latencyMs <= goldenCase.maxLatencyMs();
                String failure = latencyMs > goldenCase.maxLatencyMs() ? "latency-limit-exceeded"
                        : passed ? null : "expected-keywords-missing";
                results.add(new EvalCaseResult(goldenCase.id(), goldenCase.question(), passed,
                        response.answer(), missing, latencyMs, failure));
            } catch (RuntimeException e) {
                long latencyMs = (System.nanoTime() - caseStarted) / 1_000_000;
                results.add(new EvalCaseResult(goldenCase.id(), goldenCase.question(), false,
                        "", goldenCase.expectedKeywords(), latencyMs, e.getMessage()));
            }
        }
        long totalLatencyMs = (System.nanoTime() - started) / 1_000_000;
        int passed = (int) results.stream().filter(EvalCaseResult::passed).count();
        EvalReport report = new EvalReport(startedAt, totalLatencyMs, passed, results.size() - passed, List.copyOf(results));
        latestReport.set(report);
        return report;
    }

    public EvalReport latest() {
        return latestReport.get();
    }

    private List<GoldenCase> loadCases() {
        ClassPathResource resource = new ClassPathResource("goldens/policy-qa.yaml");
        try (InputStream inputStream = resource.getInputStream()) {
            Object parsed = new Yaml().load(inputStream);
            if (!(parsed instanceof Map<?, ?> root) || !(root.get("cases") instanceof List<?> cases)) {
                throw new IllegalStateException("Golden file must contain a cases list");
            }
            return cases.stream().map(this::toGoldenCase).toList();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load golden cases", e);
        }
    }

    private GoldenCase toGoldenCase(Object value) {
        if (!(value instanceof Map<?, ?> fields)) {
            throw new IllegalStateException("Golden case must be a YAML object");
        }
        Object keywordsValue = fields.get("expectedKeywords");
        if (!(keywordsValue instanceof List<?> keywords)) {
            throw new IllegalStateException("Golden case expectedKeywords must be a list");
        }
        return new GoldenCase(
                requiredString(fields, "id"),
                requiredString(fields, "question"),
                keywords.stream().map(String::valueOf).toList(),
                integerValue(fields.get("minKeywords"), 1),
                integerValue(fields.get("maxLatencyMs"), 20000)
        );
    }

    private String requiredString(Map<?, ?> fields, String key) {
        Object value = fields.get(key);
        if (!(value instanceof String string) || string.isBlank()) {
            throw new IllegalStateException("Golden case requires a non-empty " + key);
        }
        return string;
    }

    private int integerValue(Object value, int defaultValue) {
        return value instanceof Number number ? number.intValue() : defaultValue;
    }

    private record GoldenCase(String id, String question, List<String> expectedKeywords,
                              int minKeywords, int maxLatencyMs) {
    }
}
