package com.example.talentbridge.auth.application;

import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Signs a user out everywhere, e.g. after the password changed or the account was removed. */
@Service
@RequiredArgsConstructor
public class RevokeSessions {

    private final RefreshTokenStore refreshTokens;

    public void all(Long userId) {
        refreshTokens.deleteAllOf(userId, null);
    }

    /** Every session except the one holding {@code keepRefreshToken}. */
    public void allExcept(Long userId, String keepRefreshToken) {
        refreshTokens.deleteAllOf(userId, keepRefreshToken);
    }
}
