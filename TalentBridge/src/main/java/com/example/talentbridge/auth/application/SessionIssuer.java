package com.example.talentbridge.auth.application;

import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import com.example.talentbridge.auth.application.port.TokenIssuer;
import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.user.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/** Issues the access / refresh token pair and registers the refresh token as an open session. */
@Component
@RequiredArgsConstructor
class SessionIssuer {

    private final TokenIssuer tokenIssuer;
    private final RefreshTokenStore refreshTokens;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenSeconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenSeconds;

    AuthSession open(UserAccount account, DeviceInfo device) {
        SessionUser user = SessionUser.from(account);
        TokenClaims claims = new TokenClaims(user.email(), user.asClaim(), user.permissions());

        String refreshToken = tokenIssuer.issue(claims, Duration.ofSeconds(refreshTokenSeconds));
        refreshTokens.save(refreshToken, account.user().getId(), device, Duration.ofSeconds(refreshTokenSeconds));

        return new AuthSession(user, tokenIssuer.issue(claims, Duration.ofSeconds(accessTokenSeconds)), refreshToken);
    }
}
