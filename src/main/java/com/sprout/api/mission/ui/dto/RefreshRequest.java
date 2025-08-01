package com.sprout.api.mission.ui.dto;

import com.sprout.api.mission.application.command.RefreshCommand;

public record RefreshRequest(
    String regionCode
) {

    public RefreshCommand toCommand(Long userId, Long missionId) {
        return new RefreshCommand(userId, missionId, regionCode);
    }
}
