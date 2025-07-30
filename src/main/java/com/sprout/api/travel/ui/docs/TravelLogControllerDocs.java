package com.sprout.api.travel.ui.docs;

import com.sprout.api.travel.application.result.RegionLogResult;
import com.sprout.api.travel.application.result.TravelDetailResult;
import com.sprout.api.travel.ui.request.TravelLogCreateRequest;
import com.sprout.api.travel.ui.request.TravelLogUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "여행 일지 API 문서 모음", description = "/api/travel-logs")
public interface TravelLogControllerDocs {

    @Operation(summary = "시군구 별 내 여행 일지 조회")
    ResponseEntity<List<RegionLogResult>> getAllMyTravelLogs(String sigunguCode);

    @Operation(summary = "내 여행 일지 상세 조회")
    ResponseEntity<TravelDetailResult> getMyTravelLog(Long travelLogId);

    @Operation(summary = "여행일지 작성")
    ResponseEntity<Long> writeMyTravelLog(TravelLogCreateRequest req);

    @Operation(summary = "여행일지 수정")
    ResponseEntity<Void> updateMyTravelLog(TravelLogUpdateRequest req,Long travelLogId);

    @Operation(summary = "여행일지 전용 이미지 업로드")
    ResponseEntity<String> uploadImage(MultipartFile imageFile);
}
