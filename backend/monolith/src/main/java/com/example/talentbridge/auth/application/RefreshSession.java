package com.example.talentbridge.auth.application;

import com.example.talentbridge.shared.domain.exception.InvalidTokenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import com.example.talentbridge.auth.application.port.TokenReader;
import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.user.application.GetUserAccount;
import com.example.talentbridge.user.domain.UserAccount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshSession {

    private final TokenReader tokenReader;
    private final RefreshTokenStore refreshTokens;
    private final GetUserAccount getUserAccount;
    private final SessionIssuer sessions;

    /** A refresh token can be used once: it is revoked and replaced by a new pair. */
    public AuthSession execute(String refreshToken, DeviceInfo device) {
        String email = tokenReader.subjectOf(refreshToken);

        UserAccount account = getUserAccount.find(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Long userId = account.user().getId();

        if (!refreshTokens.isValid(refreshToken, userId))
            throw new InvalidTokenException();

        refreshTokens.delete(refreshToken, userId);
        return sessions.open(account, device);
    }
}
