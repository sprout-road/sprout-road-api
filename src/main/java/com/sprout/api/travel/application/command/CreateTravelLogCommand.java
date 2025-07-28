package com.sprout.api.travel.application.command;

import java.time.LocalDateTime;
import java.util.List;

public record CreateTravelLogCommand(
    Long userId,
    String title,
    String sigunguCode,
    LocalDateTime traveledAt,
    List<ContentBlockCommand> contents
) { }