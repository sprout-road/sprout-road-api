package com.sprout.api.admin.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "백도어 문서 모음", description = "개발용 백도어")
public interface AdminBackdoorControllerDocs {

    @Operation(summary = "개발용 가짜 지역 미션 만들기")
    void createMockMission(String regionCode);
}
