package com.sprout.api.user;

import com.sprout.api.user.TempAuthController.TempLoginDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증 API 문서 모음", description = "/api/auth")
public interface AuthControllerDocs {

    @Operation(summary = "임시 로그인 기능", description = "추후 제대로 만들 예g")
    ResponseEntity<TempLoginDto> login();
}
