package com.sprout.api.mission.application.dto;

import com.sprout.api.mission.domain.UserMissionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionDetailDto {
    private Long id;
    private String type;
    private String description;
    private Boolean completed;

    public static UserMissionDetailDto from(UserMissionDetail userMissionDetail) {
        return new UserMissionDetailDto(
            userMissionDetail.getId(),
            userMissionDetail.getType(),
            userMissionDetail.getDescription(),
            userMissionDetail.getCompleted()
        );
    }
}