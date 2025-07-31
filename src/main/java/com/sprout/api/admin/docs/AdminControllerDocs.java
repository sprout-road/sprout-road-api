package com.sprout.api.admin.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin API 문서 모음", description = "/admin/upload, /admin/map-test 에서 페이지 확인 가능")
public interface AdminControllerDocs {

    @Operation(summary = "지도 시도 정보 Json 파일 다운로드")
    ResponseEntity<byte[]> getAllSido();

    @Operation(summary = "지도 시군구 정보 Json 파일 다운로드")
    ResponseEntity<byte[]> getSigunguBySidoCode(@Pattern(regexp = "\\d{2}") String sidoCode);

    @Operation(summary = "지도 시군구 경계선 정보 Json 파일 다운로드")
    ResponseEntity<byte[]> getSidoBoundaries(@Pattern(regexp = "\\d{2}") String sidoCode);
}