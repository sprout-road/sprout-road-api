package com.sprout.api.travel.application;

import com.sprout.api.travel.application.result.RegionLogResult;
import com.sprout.api.travel.application.result.TravelDetailResult;
import com.sprout.api.travel.application.result.TravelLogSummaryResult;
import com.sprout.api.travel.domain.TravelLog;
import com.sprout.api.travel.domain.TravelLogRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    public List<RegionLogResult> getTravelLogsByRegion(String regionCode, Long userId) {
        return travelLogRepository.findAllByRegionCodeAndUserId(regionCode, userId);
    }

    public List<TravelLogSummaryResult> getTravelLogSummaryByPeriodAndRegion(
        Long userId,
        LocalDate from,
        LocalDate to,
        String regionCode
    ) {
        return travelLogRepository.findTravelLogsByUserAndPeriod(userId, from, to, regionCode);
    }
}
