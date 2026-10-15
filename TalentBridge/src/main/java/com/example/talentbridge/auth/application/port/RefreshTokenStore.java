package com.example.talentbridge.auth.application.port;

import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.auth.domain.SessionInfo;

import java.time.Duration;
import java.util.List;

/** Output port: the refresh tokens that are currently valid, one per open session. */
public interface RefreshTokenStore {

    void save(String token, Long userId, DeviceInfo device, Duration ttl);

    boolean isValid(String token, Long userId);

    void delete(String token, Long userId);

    /** Revokes the session whose id was returned by {@link #sessionsOf}. */
    void deleteBySessionId(String sessionId);

    /** Revokes every session of the user, except the one opened with {@code keepToken} (may be null). */
    void deleteAllOf(Long userId, String keepToken);

    List<SessionInfo> sessionsOf(Long userId, String currentToken);
}
