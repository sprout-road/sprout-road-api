package com.sprout.api.mission.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JsonExtractor {
    
    public String extractJsonArray(String response) {
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');
        
        if (this.isValidJsonIndices(startIndex, endIndex)) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        log.warn("유효한 JSON 배열을 찾을 수 없음: {}", response);
        throw new IllegalArgumentException("유효한 JSON 배열을 찾을 수 없습니다");
    }
    
    private boolean isValidJsonIndices(int startIndex, int endIndex) {
        return startIndex != -1 && endIndex != -1 && endIndex > startIndex;
    }
}