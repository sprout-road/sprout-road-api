package com.sprout.api.mission.application.command;

public record MissionSubmitCommand(
    Long userId,
    Long missionId,
    String regionCode,
    String type,
    String submissionContent
) {
}
