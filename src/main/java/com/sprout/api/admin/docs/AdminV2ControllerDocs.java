package com.sprout.api.admin.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Admin API 문서 모음", description = "/admin/upload, /admin/map-test 에서 페이지 확인 가능")
public interface AdminV2ControllerDocs {

    @Operation(summary = "V2 - 팔도 기준 지역 정보 Json 파일 다운로드")
    ResponseEntity<byte[]> getRegion(@PathVariable String sidoCode);
}