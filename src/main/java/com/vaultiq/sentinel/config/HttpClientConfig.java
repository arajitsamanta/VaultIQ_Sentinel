package com.vaultiq.sentinel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    RestClient vaultIqRestClient(RestClient.Builder builder, SentinelProperties properties) {
        return builder.baseUrl(properties.getVaultiqBaseUrl()).build();
    }
}
