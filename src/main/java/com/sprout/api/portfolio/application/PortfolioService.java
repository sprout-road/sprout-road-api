package com.sprout.api.portfolio.application;

import com.sprout.api.common.client.MissionClient;
import com.sprout.api.common.client.TravelLogClient;
import com.sprout.api.portfolio.application.result.PortfolioResult;
import java.time.LocalDate;
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
        Long missionCount = missionClient.getMissionCountByPeriod(from, to, userId);
        Long travelCount = travelLogClient.getTravelCountByPeriod(from, to, userId);
        return new PortfolioResult(missionCount, travelCount);
    }
}
