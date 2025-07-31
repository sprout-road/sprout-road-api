package com.sprout.api.mission.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum ParticipationStatus {
    ACTIVE("활성"),
    COMPLETED("완료");
    
    private final String description;
}