package com.vaultiq.sentinel;

import com.vaultiq.sentinel.config.SentinelProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SentinelApplicationTest {

    @Autowired
    private SentinelProperties properties;

    @Test
    void loadsDefaultGuardrailConfiguration() {
        assertThat(properties.getVaultiqBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(properties.getPii().getRules()).hasSizeGreaterThanOrEqualTo(4);
        assertThat(properties.getGrounding().getMinScore()).isEqualTo(0.5);
    }
}
