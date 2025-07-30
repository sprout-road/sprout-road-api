package com.sprout.api.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImagePurpose {

    TRAVEL_LOG("travel_log"),
    ;

    private final String value;
}
