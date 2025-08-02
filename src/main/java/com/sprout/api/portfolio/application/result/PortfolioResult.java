package com.sprout.api.portfolio.application.result;

import com.sprout.api.common.client.dto.TravelLogDto;
import java.util.List;

public record PortfolioResult(
    List<Long> missions,
    List<TravelLogDto> travelLogs
) {
}
