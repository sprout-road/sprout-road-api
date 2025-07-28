package com.sprout.api.travel.application.result;

import java.time.LocalDateTime;

public record RegionLogResult(
    LocalDateTime traveledAt,
    String title
) {
}
