package com.sprout.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class TempAuthController {

    private final TempAuthService tempAuthService;

    @PostMapping("/login")
    public ResponseEntity<TempLoginDto> login() {
        tempAuthService.signUp();
        return ResponseEntity.ok(new TempLoginDto(1L, "새싹이"));
    }

    public record TempLoginDto(Long id, String nickname) { }
}
