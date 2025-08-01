package com.sprout.api.mission.application.result;

import com.sprout.api.mission.application.dto.UserMissionDetailDto;
import com.sprout.api.mission.domain.UserMissionParticipation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyMissionResult {

    private int remainingRefreshCount;
    private List<UserMissionDetailDto> userMissions;

    public static UserDailyMissionResult from(UserMissionParticipation userMissionParticipation) {
        return new UserDailyMissionResult(
            userMissionParticipation.getRemainingRefreshCount(),
            extractUserMissions(userMissionParticipation)
        );
    }

    private static List<UserMissionDetailDto> extractUserMissions(UserMissionParticipation userMissionParticipation) {
        return userMissionParticipation.getMissions()
            .stream()
            .map(UserMissionDetailDto::from)
            .toList();
    }
}
