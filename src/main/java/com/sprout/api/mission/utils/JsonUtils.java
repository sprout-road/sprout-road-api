package com.sprout.api.mission.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprout.api.common.utils.ObjectValidator;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Integer> parseStringToIntList(String jsonString) {
        ObjectValidator.validateNotBlank(jsonString, "blank json string");
        
        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류: {}", jsonString, e);
            return new ArrayList<>();
        }
    }

    public static String convertIntListToString(List<Integer> intList) {
        if (intList == null || intList.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(intList);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 오류: {}", intList, e);
            return "[]";
        }
    }
    
    /**
     * 초기 미션 설정용 헬퍼 메서드
     */
    public static String getInitialVisibleMissions() {
        return "[1,2,3,4,5]";
    }
    
    public static String getInitialShownMissions() {
        return "[1,2,3,4,5]";
    }
}