package com.sprout.api.mission.utils;

import com.sprout.api.mission.application.dto.AiMissionResponse;
import java.util.List;

public class DefaultMissionProvider {
    
    public static List<AiMissionResponse> getDefaultMissions() {
        return List.of(
            new AiMissionResponse("picture", "지역 대표 관광지에서 인증샷 찍기"),
            new AiMissionResponse("writing", "지역 특색 체험 후 소감 쓰기"),
            new AiMissionResponse("picture", "지역 명소에서 풍경 사진 찍기"),
            new AiMissionResponse("writing", "지역 문화 체험 후 후기 작성하기"),
            new AiMissionResponse("picture", "지역 전통시장에서 특산품과 함께 사진 찍기"),
            new AiMissionResponse("writing", "새로 알게된 지역의 특징을 작성하기"),
            new AiMissionResponse("picture", "지역 주민과 함께 사진 찍기"),
            new AiMissionResponse("writing", "지역의 방문하기 전과 후의 차이점 작성하기"),
            new AiMissionResponse("picture", "지역을 표현할 수 있는 간단한 그림을 그리고 촬영하기"),
            new AiMissionResponse("writing", "지역에 대해서 한가지 추천해보기")
        );
    }
}