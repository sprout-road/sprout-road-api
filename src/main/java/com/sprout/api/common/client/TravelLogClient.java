package com.sprout.api.common.client;

import com.sprout.api.common.client.dto.TravelLogDto;
import java.time.LocalDate;
import java.util.List;

public interface TravelLogClient {

    List<TravelLogDto> getTravelLogsByPeriod(LocalDate from, LocalDate to, Long userId);
}
