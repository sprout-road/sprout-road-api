package com.sprout.api.common.client;

import java.time.LocalDate;
import java.util.List;

public interface MissionClient {

    List<Long> getMissionsByPeriod(LocalDate from, LocalDate to, Long userId);
}
