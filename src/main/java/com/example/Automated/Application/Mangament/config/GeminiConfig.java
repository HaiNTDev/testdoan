package com.example.Automated.Application.Mangament.config;

// ⭐️ Import class Client chính xác
import com.google.genai.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {


    @Bean
    public Client geminiClient(@Value("${gemini.api.key}") String apiKey) {

        if (apiKey == null || apiKey.trim().isEmpty()) {

            throw new IllegalArgumentException("Cấu hình LỖI: Không tìm thấy giá trị cho 'gemini.api.key'. Kiểm tra file application.properties và profile.");
        }


        return Client.builder()
                .apiKey(apiKey)
                .build();
    }


    @Bean("fileDownloaderWebClient")
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}