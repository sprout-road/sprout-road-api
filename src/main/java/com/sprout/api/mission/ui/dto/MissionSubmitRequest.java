package com.sprout.api.mission.ui.dto;

import com.sprout.api.mission.application.command.MissionSubmitCommand;

public record MissionSubmitRequest(
    String type,
    String submissionContent
) {

    public MissionSubmitCommand toCommand(Long userId, Long missionId, String regionCode) {
        return new MissionSubmitCommand(userId, missionId, regionCode, type, submissionContent);
    }
}
