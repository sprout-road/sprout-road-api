package com.sprout.api.travel.ui.docs;

import com.sprout.api.travel.application.result.RegionLogResult;
import com.sprout.api.travel.application.result.TravelDetailResult;
import com.sprout.api.travel.application.result.TravelLogSummaryResult;
import com.sprout.api.travel.ui.docs.annotation.TravelLogCreateDocs;
import com.sprout.api.travel.ui.docs.annotation.TravelLogDetailDocs;
import com.sprout.api.travel.ui.docs.annotation.TravelLogUpdateDocs;
import com.sprout.api.travel.ui.request.TravelLogCreateRequest;
import com.sprout.api.travel.ui.request.TravelLogUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "여행 일지 API 문서 모음", description = "/api/travel-logs")
public interface TravelLogControllerDocs {

    @Operation(summary = "시군구 별 내 여행 일지 조회")
    ResponseEntity<List<RegionLogResult>> getAllMyTravelLogs(String sigunguCode);

    @TravelLogDetailDocs
    ResponseEntity<TravelDetailResult> getMyTravelLog(Long travelLogId);

    @TravelLogCreateDocs
    ResponseEntity<Long> writeMyTravelLog(TravelLogCreateRequest req);

    @TravelLogUpdateDocs
    ResponseEntity<Void> updateMyTravelLog(TravelLogUpdateRequest req, Long travelLogId);

    @Operation(summary = "여행일지 전용 이미지 업로드")
    ResponseEntity<String> uploadImage(MultipartFile imageFile);

    @Operation(summary = "사용자 기간 별 여행일지 조히 (포트폴리오)")
    ResponseEntity<List<TravelLogSummaryResult>> getUserTravelLogPeriod(
        Long userId,
        LocalDate from,
        LocalDate to,
        String regionCode
    );
}
