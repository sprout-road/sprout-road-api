package com.sprout.api.travel.application.result;

import java.time.LocalDateTime;

public record RegionLogResult(
    Long id,
    LocalDateTime traveledAt,
    String title
) {
}
