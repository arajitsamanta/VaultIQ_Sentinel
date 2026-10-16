package com.vaultiq.sentinel.controller;

import com.vaultiq.sentinel.dto.MetricsResponse;
import com.vaultiq.sentinel.service.MetricsStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel/metrics")
public class MetricsController {

    private final MetricsStore metricsStore;

    public MetricsController(MetricsStore metricsStore) {
        this.metricsStore = metricsStore;
    }

    @GetMapping
    public MetricsResponse metrics() {
        return metricsStore.snapshot();
    }
}
