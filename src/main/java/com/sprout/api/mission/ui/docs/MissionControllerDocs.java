package com.sprout.api.mission.ui.docs;

import com.sprout.api.mission.application.result.UserDailyMissionResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "미션 API 모음")
public interface MissionControllerDocs {

    @Operation(summary = "사용자가 지역의 데일리 미션을 시작했는지 확인하는 API")
    ResponseEntity<Boolean> getMissionStatus(String regionCode);

    @Operation(summary = "사용자가 지역의 데일리 미션을 시작하는 API")
    ResponseEntity<UserDailyMissionResult> startMission(String regionCode);

    @Operation(summary = "현재 지역의 데일리 미션을 조회하는 API")
    ResponseEntity<UserDailyMissionResult> getMissions(String regionCode);
}
