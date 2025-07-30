package com.sprout.api.travel.application.result;

import java.time.LocalDate;

public record RegionLogResult(
    Long id,
    LocalDate traveledAt,
    String title
) {
}
