package com.sprout.api.travel.ui;

import com.sprout.api.travel.application.TravelLogQueryService;
import com.sprout.api.travel.application.TravelLogService;
import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import com.sprout.api.travel.application.result.TravelDetailResult;
import com.sprout.api.travel.ui.request.TravelLogRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel-logs")
public class TravelLogController {

    private final TravelLogService travelLogService;
    private final TravelLogQueryService travelLogQueryService;

    @GetMapping
    public ResponseEntity<List<String>> getAllMyTravelLogs(@RequestParam String sidoCode) {
        // 임시 사용자 정보로만 진행 -> 인증 및 사용자 컨텍스트는 제일 마지막
        Long userId = 1L;
        /*
            todo: 전체 여행 일지 가져오기
             return {
                "날짜":
                "제목":
             }
         */
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{logId}")
    public ResponseEntity<TravelDetailResult> getMyTravelLog(@PathVariable Long logId) {
        Long userId = 1L;
        TravelDetailResult result = travelLogQueryService.getMyTravelLog(logId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Long> writeMyTravelLog(@RequestBody TravelLogRequest req) {
        Long userId = 1L;
        CreateTravelLogCommand command = req.toCommand(userId);
        Long result = travelLogService.writeTravelLog(command);
        return ResponseEntity.ok(result);
    }
}
