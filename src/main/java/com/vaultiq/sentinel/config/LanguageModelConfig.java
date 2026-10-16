package com.vaultiq.sentinel.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LanguageModelConfig {

    @Bean
    ChatLanguageModel groundingChatLanguageModel(
            @Value("${sentinel.llm.base-url:http://localhost:11434/v1}") String baseUrl,
            @Value("${sentinel.llm.api-key:ollama}") String apiKey,
            @Value("${sentinel.model:llama3.2}") String model,
            @Value("${sentinel.llm.temperature:0.0}") double temperature) {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(model)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();
    }
}
