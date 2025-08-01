package com.sprout.api.mission.infrastructure.config;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GoogleAiConfig {

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Bean
    public Client geminiClient() {
        try {
            return new Client.Builder()
                .apiKey(apiKey)
                .build();
        } catch (Exception e) {
            log.error("Gemini Client 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini Client 초기화 실패: " + e.getMessage(), e);
        }
    }
    @Bean
    public GenerateContentConfig missionGenerateConfig() {
        return GenerateContentConfig.builder()
            .temperature(0.7f)      // 창의성 높임
            .maxOutputTokens(1000)  // 미션 설명이 더 풍부
            .topP(0.9f)            // 다양한 선택지 허용
            .topK(40f)             // 적당히 제한적
            .build();
    }
}