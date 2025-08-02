package com.sprout.api.common.client;

import java.time.LocalDate;

public interface MissionClient {

    Long getMissionCountByPeriod(LocalDate from, LocalDate to, Long userId, String regionCode);
}
