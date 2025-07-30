package com.sprout.api.mission.infrastructure;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeminiTemplate {

    private final String model;
    private final Client geminiClient;
    private final GenerateContentConfig defaultConfig;

    public GeminiTemplate (
        @Value("${google.ai.model}")
        String model,
        Client geminiClient,
        GenerateContentConfig defaultConfig
    ) {
        this.model = model;
        this.geminiClient = geminiClient;
        this.defaultConfig = defaultConfig;
    }

    // 커스텀 설정으로 생성
    public String generate(String prompt) {
        try {
            log.info("Gemini API 호출 - 모델: {}, 프롬프트: {}", model, prompt);
            GenerateContentResponse response = geminiClient.models.generateContent(model, prompt, defaultConfig);
            return response.text();
        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage(), e);
            throw new IllegalArgumentException("AI 요청 실패: " + e.getMessage(), e);
        }
    }
}

