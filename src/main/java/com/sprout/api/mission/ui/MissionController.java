package com.sprout.api.mission.ui;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.common.utils.FileMetaDataExtractor;
import com.sprout.api.mission.application.UserMissionQueryService;
import com.sprout.api.mission.application.UserMissionService;
import com.sprout.api.mission.application.command.MissionSubmitCommand;
import com.sprout.api.mission.application.command.RefreshCommand;
import com.sprout.api.mission.application.result.UserDailyMissionResult;
import com.sprout.api.mission.domain.UserMissionRepository;
import com.sprout.api.mission.domain.dto.RegionMissionCountDto;
import com.sprout.api.mission.ui.docs.MissionControllerDocs;
import com.sprout.api.mission.ui.dto.MissionSubmitRequest;
import com.sprout.api.mission.ui.response.MissionSummaryResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController implements MissionControllerDocs {

    private final UserMissionQueryService userMissionQueryService;
    private final UserMissionService userMissionService;
    private final UserMissionRepository userMissionRepository;
    private final FileMetaDataExtractor fileMetaDataExtractor;
    private final ImageManageClient imageManageClient;

    @GetMapping("/regions/{regionCode}/status")
    public ResponseEntity<Boolean> getMissionStatus(@PathVariable String regionCode) {
        Long userId = 1L;
        boolean result = userMissionQueryService.existsTodayMission(regionCode, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/regions/{regionCode}/start")
    public ResponseEntity<UserDailyMissionResult> startMission(@PathVariable String regionCode) {
        Long userId = 1L; // 임시 사용자
        UserDailyMissionResult result = userMissionService.startTodayMission(userId, regionCode);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/regions/{regionCode}")
    public ResponseEntity<UserDailyMissionResult> getMissions(@PathVariable String regionCode) {
        Long userId = 1L; // 임시 사용자
        UserDailyMissionResult result = userMissionQueryService.getTodayMissions(userId, regionCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{missionId}/regions/{regionCode}/refresh")
    public ResponseEntity<UserDailyMissionResult> refresh(
        @PathVariable Long missionId,
        @PathVariable String regionCode
    ) {
        Long userId = 1L; // 임시 사용자
        RefreshCommand command = new RefreshCommand(userId, missionId, regionCode);
        UserDailyMissionResult result = userMissionService.refresh(command);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{missionId}/regions/{regionCode}/submit")
    public ResponseEntity<String> submit(
        @PathVariable Long missionId,
        @PathVariable String regionCode,
        @RequestBody MissionSubmitRequest request
    ) {
        Long userId = 1L;
        MissionSubmitCommand command = request.toCommand(userId, missionId, regionCode);
        String reward = userMissionService.submitMission(command);
        return ResponseEntity.ok(reward);
    }

    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile imageFile) {
        FileMetaData imageMetaData = fileMetaDataExtractor.extractFrom(imageFile);
        String imageUrl = imageManageClient.uploadImage(imageMetaData, ImagePurpose.DAILY_MISSION);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RegionMissionCountDto>> getMissionHistory() {
        Long userId = 1L;
        List<RegionMissionCountDto> result = userMissionRepository.findCompletedMissionCountByRegion(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}/period")
    public ResponseEntity<?> getUserMissionPeriod(
        @PathVariable Long userId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to,
        @RequestParam String regionCode
    ) {
        List<MissionSummaryResponse> result
            = userMissionRepository.findCompletedMissionIdsByUserAndPeriod(userId, from, to, regionCode);
        return ResponseEntity.ok(result);
    }
}
