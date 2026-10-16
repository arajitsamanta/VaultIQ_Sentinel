package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.config.SentinelProperties;
import com.vaultiq.sentinel.dto.CitationDto;
import com.vaultiq.sentinel.dto.GuardrailReport;
import com.vaultiq.sentinel.dto.GuardedChatResponse;
import com.vaultiq.sentinel.dto.UpstreamServiceException;
import com.vaultiq.sentinel.dto.VaultIqChatRequest;
import com.vaultiq.sentinel.dto.VaultIqChatResponse;
import com.vaultiq.sentinel.model.RequestMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

@Service
public class GuardedChatService implements GuardedChat {

    private static final Logger log = LoggerFactory.getLogger(GuardedChatService.class);
    private static final String INJECTION_CLEAN = "clean";

    private final RestClient vaultIqRestClient;
    private final PiiRedactor piiRedactor;
    private final InjectionScreen injectionScreen;
    private final GroundingEvaluator groundingEvaluator;
    private final CostCalculator costCalculator;
    private final MetricsStore metricsStore;
    private final SentinelProperties properties;

    public GuardedChatService(
            RestClient vaultIqRestClient,
            PiiRedactor piiRedactor,
            InjectionScreen injectionScreen,
            GroundingEvaluator groundingEvaluator,
            CostCalculator costCalculator,
            MetricsStore metricsStore,
            SentinelProperties properties) {
        this.vaultIqRestClient = vaultIqRestClient;
        this.piiRedactor = piiRedactor;
        this.injectionScreen = injectionScreen;
        this.groundingEvaluator = groundingEvaluator;
        this.costCalculator = costCalculator;
        this.metricsStore = metricsStore;
        this.properties = properties;
    }

    @Override
    public GuardedChatResponse chat(String question) {
        long started = System.nanoTime();
        RedactionResult safeQuestion = piiRedactor.redact(question);
        injectionScreen.check(safeQuestion.text());

        VaultIqChatResponse upstream;
        try {
            upstream = vaultIqRestClient.post()
                    .uri("/api/chat")
                    .body(new VaultIqChatRequest(safeQuestion.text()))
                    .retrieve()
                    .body(VaultIqChatResponse.class);
        } catch (RuntimeException e) {
            throw new UpstreamServiceException("VaultIQ chat request failed", e);
        }
        if (upstream == null || upstream.answer() == null) {
            throw new UpstreamServiceException("VaultIQ returned an empty chat response", null);
        }

        RedactionResult safeAnswer = piiRedactor.redact(upstream.answer());
        List<SanitizedCitation> sanitizedCitations = upstream.citations() == null ? List.of() : upstream.citations().stream()
                .map(citation -> {
                    RedactionResult safeText = piiRedactor.redact(citation.text() == null ? "" : citation.text());
                    RedactionResult safeName = piiRedactor.redact(citation.documentName() == null ? "" : citation.documentName());
                    return new SanitizedCitation(new CitationDto(citation.documentId(), safeName.text(),
                            citation.chunkIndex(), safeText.text(), citation.relevanceScore()),
                            safeText.redactions() + safeName.redactions());
                }).toList();
        int outputRedactions = safeAnswer.redactions()
                + sanitizedCitations.stream().mapToInt(SanitizedCitation::redactions).sum();
        List<CitationDto> safeCitations = sanitizedCitations.stream()
                .map(SanitizedCitation::citation)
                .toList();
        String context = safeCitations.stream().map(CitationDto::text).reduce("", (left, right) -> left + "\n" + right);

        GroundingEvaluator.Evaluation grounding = groundingEvaluator.evaluate(safeAnswer.text(), context);
        String answer = grounding.grounded() ? safeAnswer.text() : properties.getGrounding().getFallback();
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;

        long inputTokens = costCalculator.estimateTokens(safeQuestion.text() + "\n" + context);
        long outputTokens = costCalculator.estimateTokens(safeAnswer.text());
        double proxyCost = costCalculator.calculate(inputTokens, outputTokens,
                properties.getPricing().getInputPer1k(), properties.getPricing().getOutputPer1k());
        double totalCost = proxyCost + grounding.costUsd();
        metricsStore.record(new RequestMetric(Instant.now(), properties.getModel(),
                inputTokens + grounding.inputTokens(), outputTokens + grounding.outputTokens(),
                totalCost, elapsedMs, true));

        GuardrailReport report = new GuardrailReport(
                safeQuestion.redactions() + outputRedactions,
                safeQuestion.redactions(),
                outputRedactions,
                INJECTION_CLEAN,
                grounding.score(),
                grounding.grounded(),
                totalCost,
                elapsedMs,
                properties.getModel(),
                true);
        log.info("Guarded chat completed: grounded={}, latencyMs={}, piiRedactions={}",
                grounding.grounded(), elapsedMs, report.piiRedactions());
        return new GuardedChatResponse(safeQuestion.text(), answer, safeCitations, report);
    }

    private record SanitizedCitation(CitationDto citation, int redactions) {
    }
}
