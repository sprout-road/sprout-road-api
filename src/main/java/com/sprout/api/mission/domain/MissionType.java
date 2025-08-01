package com.sprout.api.mission.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MissionType {
    PICTURE("picture"),
    WRITING("writing"),;
    
    private final String value;

    public static MissionType of(String value){
        for (MissionType missionType : MissionType.values()) {
            if(missionType.value.equals(value)){
                return missionType;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 미션 타입");
    }
}