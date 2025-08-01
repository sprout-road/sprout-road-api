package com.sprout.api.mission.application.command;

public record RefreshCommand(
    Long userId,
    Long missionId,
    String regionCode,
    Integer position
) {
}
