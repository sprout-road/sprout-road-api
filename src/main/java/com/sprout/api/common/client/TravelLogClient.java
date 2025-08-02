package com.sprout.api.common.client;

import java.time.LocalDate;

public interface TravelLogClient {

    Long getTravelCountByPeriod(LocalDate from, LocalDate to, Long userId);
}
