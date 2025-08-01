package com.sprout.api.mission.application;

import com.sprout.api.mission.application.dto.AiMissionResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DefaultMissionProvider {
    
    public List<AiMissionResponse> getDefaultMissions() {
        return List.of(
            new AiMissionResponse("picture", "지역 대표 관광지에서 인증샷 찍기"),
            new AiMissionResponse("writing", "지역 특색 체험 후 소감 쓰기"),
            new AiMissionResponse("picture", "지역 명소에서 풍경 사진 찍기"),
            new AiMissionResponse("writing", "지역 문화 체험 후 후기 작성하기"),
            new AiMissionResponse("picture", "지역 전통시장에서 특산품과 함께 사진 찍기")
        );
    }
}