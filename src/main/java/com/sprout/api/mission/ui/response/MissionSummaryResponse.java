package com.sprout.api.mission.ui.response;

import java.time.LocalDate;

public record MissionSummaryResponse(
    Long id,
    LocalDate completedAt,
    String description
) {
}
