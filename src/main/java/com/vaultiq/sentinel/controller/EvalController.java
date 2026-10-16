package com.vaultiq.sentinel.controller;

import com.vaultiq.sentinel.dto.EvalReport;
import com.vaultiq.sentinel.service.RegressionRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel/evals/regression")
public class EvalController {

    private final RegressionRunner regressionRunner;

    public EvalController(RegressionRunner regressionRunner) {
        this.regressionRunner = regressionRunner;
    }

    @PostMapping("/run")
    @ResponseStatus(HttpStatus.OK)
    public EvalReport run() {
        return regressionRunner.run();
    }

    @GetMapping("/latest")
    public ResponseEntity<EvalReport> latest() {
        EvalReport report = regressionRunner.latest();
        return report == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(report);
    }
}
