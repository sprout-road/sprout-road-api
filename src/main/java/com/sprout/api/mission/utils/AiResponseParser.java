package com.sprout.api.mission.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprout.api.mission.application.dto.AiMissionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiResponseParser {
    
    private final JsonExtractor jsonExtractor;
    private final ObjectMapper objectMapper;
    
    public List<AiMissionResponse> parseToMissions(String aiResponse) {
        try {
            return this.parseAiResponse(aiResponse);
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패, 기본 미션 사용: {}", e.getMessage());
            return DefaultMissionProvider.getDefaultMissions();
        }
    }
    
    private List<AiMissionResponse> parseAiResponse(String aiResponse) throws JsonProcessingException {
        String jsonString = jsonExtractor.extractJsonArray(aiResponse);
        return objectMapper.readValue(jsonString, new TypeReference<>() {});
    }
}