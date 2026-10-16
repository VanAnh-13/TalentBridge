package com.example.talentbridge.auth.application;

import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import com.example.talentbridge.auth.application.port.TokenReader;
import com.example.talentbridge.auth.domain.SessionInfo;
import com.example.talentbridge.user.application.UserDirectory;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManageSessions {

    private final TokenReader tokenReader;
    private final RefreshTokenStore refreshTokens;
    private final UserDirectory users;
    private final CurrentUser currentUser;

    /** The open sessions of the owner of the refresh token, flagging the one that token belongs to. */
    public List<SessionInfo> list(String refreshToken) {
        UserDirectory.UserSummary user = users.findByEmail(tokenReader.subjectOf(refreshToken))
                .orElseThrow(() -> new NotFoundException("User not found"));
        return refreshTokens.sessionsOf(user.id(), refreshToken);
    }

    /** A session id looks like "auth::refresh_token:{userId}:{hash}"; only its owner may revoke it. */
    public void revoke(String sessionId) {
        String[] part = sessionId.split(":");
        String sessionUserId = part[3];

        UserDirectory.UserSummary me = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!me.id().toString().equalsIgnoreCase(sessionUserId))
            throw new ForbiddenException("Access denied");

        refreshTokens.deleteBySessionId(sessionId);
    }
}
