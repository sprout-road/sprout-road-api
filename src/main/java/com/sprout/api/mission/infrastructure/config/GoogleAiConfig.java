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
            .temperature(0.1f)      // 매우 낮음 -> JSON 일관성
            .maxOutputTokens(800)   // 미션 5개에 적당한 크기
            .topP(0.7f)            // 제한적 선택
            .topK(10f)              // 매우 제한적
            .build();
    }
}