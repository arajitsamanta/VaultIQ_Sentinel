package com.vaultiq.sentinel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "sentinel")
public class SentinelProperties {

    private String vaultiqBaseUrl = "http://localhost:8080";
    private String model = "llama3.2";
    private Pii pii = new Pii();
    private Grounding grounding = new Grounding();
    private Pricing pricing = new Pricing();
    private Budget budget = new Budget();
    private List<String> injectionPatterns = new ArrayList<>();

    public String getVaultiqBaseUrl() {
        return vaultiqBaseUrl;
    }

    public void setVaultiqBaseUrl(String vaultiqBaseUrl) {
        this.vaultiqBaseUrl = vaultiqBaseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Pii getPii() {
        return pii;
    }

    public void setPii(Pii pii) {
        this.pii = pii;
    }

    public Grounding getGrounding() {
        return grounding;
    }

    public void setGrounding(Grounding grounding) {
        this.grounding = grounding;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public List<String> getInjectionPatterns() {
        return injectionPatterns;
    }

    public void setInjectionPatterns(List<String> injectionPatterns) {
        this.injectionPatterns = injectionPatterns;
    }

    public static class Pii {
        private boolean modelPass;
        private List<Rule> rules = new ArrayList<>();

        public boolean isModelPass() {
            return modelPass;
        }

        public void setModelPass(boolean modelPass) {
            this.modelPass = modelPass;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }

    public static class Rule {
        private String label;
        private String pattern;

        public Rule() {
        }

        public Rule(String label, String pattern) {
            this.label = label;
            this.pattern = pattern;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }

    public static class Grounding {
        private double minScore = 0.5;
        private String fallback = "I could not verify that answer against the provided documents.";

        public double getMinScore() {
            return minScore;
        }

        public void setMinScore(double minScore) {
            this.minScore = minScore;
        }

        public String getFallback() {
            return fallback;
        }

        public void setFallback(String fallback) {
            this.fallback = fallback;
        }
    }

    public static class Pricing {
        private double inputPer1k;
        private double outputPer1k;

        public double getInputPer1k() {
            return inputPer1k;
        }

        public void setInputPer1k(double inputPer1k) {
            this.inputPer1k = inputPer1k;
        }

        public double getOutputPer1k() {
            return outputPer1k;
        }

        public void setOutputPer1k(double outputPer1k) {
            this.outputPer1k = outputPer1k;
        }
    }

    public static class Budget {
        private double dailyUsdCap = 5.0;

        public double getDailyUsdCap() {
            return dailyUsdCap;
        }

        public void setDailyUsdCap(double dailyUsdCap) {
            this.dailyUsdCap = dailyUsdCap;
        }
    }
}
