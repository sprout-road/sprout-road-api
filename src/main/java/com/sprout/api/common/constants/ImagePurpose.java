package com.sprout.api.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImagePurpose {

    TRAVEL_LOG("travel_log"),
    DAILY_MISSION("daily_mission"),
    ;

    private final String value;
}
