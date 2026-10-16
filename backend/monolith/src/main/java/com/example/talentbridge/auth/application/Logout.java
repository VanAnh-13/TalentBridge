package com.example.talentbridge.auth.application;

import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import com.example.talentbridge.auth.application.port.TokenReader;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.application.UserDirectory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class Logout {

    private final TokenReader tokenReader;
    private final RefreshTokenStore refreshTokens;
    private final UserDirectory users;

    /** Ends the session of the given refresh token; without a token there is nothing to end. */
    public void execute(String refreshToken) {
        if (refreshToken == null) return;

        UserDirectory.UserSummary user = users.findByEmail(tokenReader.subjectOf(refreshToken))
                .orElseThrow(() -> new NotFoundException("User not found"));
        refreshTokens.delete(refreshToken, user.id());
    }
}
