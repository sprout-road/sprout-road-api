package com.sprout.api.mission.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionMissionCountDto {
    private String regionCode;
    private long count;
}