package com.sprout.api.travel.ui;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.common.utils.FileMetaDataExtractor;
import com.sprout.api.travel.application.TravelLogQueryService;
import com.sprout.api.travel.application.TravelLogService;
import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import com.sprout.api.travel.application.command.UpdateTravelLogCommand;
import com.sprout.api.travel.application.result.RegionLogResult;
import com.sprout.api.travel.application.result.TravelDetailResult;
import com.sprout.api.travel.application.result.TravelLogSummaryResult;
import com.sprout.api.travel.ui.docs.TravelLogControllerDocs;
import com.sprout.api.travel.ui.request.TravelLogCreateRequest;
import com.sprout.api.travel.ui.request.TravelLogUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel-logs")
public class TravelLogController implements TravelLogControllerDocs {

    private final TravelLogService travelLogService;
    private final TravelLogQueryService travelLogQueryService;
    private final FileMetaDataExtractor fileMetaDataExtractor;
    private final ImageManageClient imageManageClient;

    @GetMapping
    public ResponseEntity<List<RegionLogResult>> getAllMyTravelLogs(@RequestParam String regionCode) {
        // 임시 사용자 정보로만 진행 -> 인증 및 사용자 컨텍스트는 제일 마지막
        Long userId = 1L;
        List<RegionLogResult> result = travelLogQueryService.getTravelLogsByRegion(regionCode, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{travelLogId}")
    public ResponseEntity<TravelDetailResult> getMyTravelLog(@PathVariable Long travelLogId) {
        Long userId = 1L;
        TravelDetailResult result = travelLogQueryService.getMyTravelLog(travelLogId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Long> writeMyTravelLog(@RequestBody TravelLogCreateRequest req) {
        Long userId = 1L;
        CreateTravelLogCommand command = req.toCommand(userId);
        Long result = travelLogService.writeTravelLog(command);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{travelLogId}")
    public ResponseEntity<Void> updateMyTravelLog(
        @RequestBody TravelLogUpdateRequest req,
        @PathVariable Long travelLogId
    ) {
        Long userId = 1L;
        UpdateTravelLogCommand command = req.toCommand(travelLogId, userId);
        travelLogService.updateTravelLog(command);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile imageFile) {
        FileMetaData imageMetaData = fileMetaDataExtractor.extractFrom(imageFile);
        String imageUrl = imageManageClient.uploadImage(imageMetaData, ImagePurpose.TRAVEL_LOG);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/users/{userId}/period")
    public ResponseEntity<List<TravelLogSummaryResult>> getUserTravelLogPeriod(
        @PathVariable Long userId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to,
        @RequestParam String regionCode
    ) {
        List<TravelLogSummaryResult> result =
            travelLogQueryService.getTravelLogSummaryByPeriodAndRegion(userId, from, to, regionCode);
        return ResponseEntity.ok(result);
    }
}
