package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.config.SentinelProperties;
import com.vaultiq.sentinel.dto.MetricsResponse;
import com.vaultiq.sentinel.model.RequestMetric;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class MetricsStore {

    private static final int MAX_ENTRIES = 1000;
    private final ConcurrentLinkedDeque<RequestMetric> requests = new ConcurrentLinkedDeque<>();
    private final SentinelProperties properties;

    public MetricsStore(SentinelProperties properties) {
        this.properties = properties;
    }

    public void record(RequestMetric metric) {
        requests.addFirst(metric);
        while (requests.size() > MAX_ENTRIES) {
            requests.pollLast();
        }
    }

    public MetricsResponse snapshot() {
        List<RequestMetric> all = List.copyOf(requests);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<RequestMetric> todays = all.stream()
                .filter(metric -> metric.timestamp().atZone(ZoneOffset.UTC).toLocalDate().equals(today))
                .toList();
        long inputTokens = todays.stream().mapToLong(RequestMetric::inputTokens).sum();
        long outputTokens = todays.stream().mapToLong(RequestMetric::outputTokens).sum();
        double totalCost = todays.stream().mapToDouble(RequestMetric::costUsd).sum();
        double cap = properties.getBudget().getDailyUsdCap();
        return new MetricsResponse(all, inputTokens, outputTokens, totalCost, cap, totalCost > cap);
    }
}
