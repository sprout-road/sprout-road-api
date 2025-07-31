package com.sprout.api.mission.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MissionType {
    PICTURE("사진"),
    WRITING("글쓰기"),;
    
    private final String description;
}