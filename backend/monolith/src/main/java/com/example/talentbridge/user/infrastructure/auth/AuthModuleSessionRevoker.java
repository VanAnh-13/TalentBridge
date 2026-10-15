package com.example.talentbridge.user.infrastructure.auth;

import com.example.talentbridge.auth.application.RevokeSessions;
import com.example.talentbridge.user.application.port.SessionRevoker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapter: sessions belong to the auth module. */
@Component
@RequiredArgsConstructor
public class AuthModuleSessionRevoker implements SessionRevoker {

    private final RevokeSessions revokeSessions;

    @Override
    public void revokeAll(Long userId) {
        revokeSessions.all(userId);
    }

    @Override
    public void revokeAllExcept(Long userId, String keepRefreshToken) {
        revokeSessions.allExcept(userId, keepRefreshToken);
    }
}
