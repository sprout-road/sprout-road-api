package com.sprout.api.mission.ui.docs;

import com.sprout.api.mission.application.result.UserDailyMissionResult;
import com.sprout.api.mission.domain.dto.RegionMissionCountDto;
import com.sprout.api.mission.ui.dto.MissionSubmitRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "미션 API 모음")
public interface MissionControllerDocs {

    @Operation(summary = "사용자가 지역의 데일리 미션을 시작했는지 확인하는 API")
    ResponseEntity<Boolean> getMissionStatus(String regionCode);

    @Operation(summary = "사용자가 지역의 데일리 미션을 시작하는 API")
    ResponseEntity<UserDailyMissionResult> startMission(String regionCode);

    @Operation(summary = "현재 지역의 데일리 미션을 조회하는 API")
    ResponseEntity<UserDailyMissionResult> getMissions(String regionCode);

    @Operation(summary = "데일리 미션을 새로고침하는 API")
    ResponseEntity<UserDailyMissionResult> refresh(Long missionId, String regionCode);

    @Operation(summary = "데일리 미션을 새로고침하는 API")
    ResponseEntity<String> submit(Long missionId, String regionCode, MissionSubmitRequest request);

    @Operation(summary = "데일리 미션 인증 이미지를 업로드하는 API")
    ResponseEntity<String> uploadImage(MultipartFile imageFile);

    @Operation(summary = "모든 지역 미션 완료 내역을 확인하는 API")
    ResponseEntity<List<RegionMissionCountDto>> getMissionHistory();
}
