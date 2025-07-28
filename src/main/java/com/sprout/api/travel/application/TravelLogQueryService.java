package com.sprout.api.travel.application;

import com.sprout.api.travel.application.result.RegionLogResult;
import com.sprout.api.travel.application.result.TravelDetailResult;
import com.sprout.api.travel.domain.TravelLog;
import com.sprout.api.travel.domain.TravelLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelLogQueryService {

    private final TravelLogRepository travelLogRepository;

    public TravelDetailResult getMyTravelLog(Long logId, Long userId) {
        TravelLog travelLog = travelLogRepository.findById(logId).orElseThrow(IllegalArgumentException::new);
        if (!travelLog.getUserId().equals(userId)) {
            throw new IllegalArgumentException();
        }
        return TravelDetailResult.of(travelLog);
    }

    public List<RegionLogResult> getTravelLogsByRegion(String sidoCode, Long userId) {
        return travelLogRepository.findAllBySigunguCodeAndUserId(sidoCode, userId);
    }
}
