package com.example.talentbridge.auth.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/** The http-only cookie that carries the refresh token. */
@Component
class RefreshCookies {

    static final String NAME = "refresh_token";

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenSeconds;

    ResponseCookie issue(String refreshToken) {
        return ResponseCookie.from(NAME, refreshToken)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshTokenSeconds)
                .build();
    }

    ResponseCookie clear() {
        return ResponseCookie.from(NAME, "")
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }
}
