package com.sprout.api.portfolio.application;

import com.sprout.api.common.client.MissionClient;
import com.sprout.api.common.client.TravelLogClient;
import com.sprout.api.common.client.dto.TravelLogDto;
import com.sprout.api.portfolio.application.result.PortfolioResult;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortfolioService {

    private final MissionClient missionClient;
    private final TravelLogClient travelLogClient;

    public PortfolioResult getUserPortfolioByPeriod(Long userId, LocalDate from, LocalDate to) {
        List<Long> missionsByPeriod = missionClient.getMissionsByPeriod(from, to, userId);
        List<TravelLogDto> travelLogsByPeriod = travelLogClient.getTravelLogsByPeriod(from, to, userId);
        return new PortfolioResult(missionsByPeriod, travelLogsByPeriod);
    }
}
