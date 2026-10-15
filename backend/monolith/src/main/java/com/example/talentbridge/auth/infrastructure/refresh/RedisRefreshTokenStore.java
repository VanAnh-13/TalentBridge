package com.example.talentbridge.auth.infrastructure.refresh;

import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.auth.domain.SessionInfo;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "spring.cache.type",
        havingValue = "redis",
        matchIfMissing = false
)
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final RedisTemplate<String, StoredSession> redisStoredSessionTemplate;

    private String buildKey(String token, Long userId) {
        return "auth::refresh_token:" + userId + ":" + DigestUtils.sha256Hex(token);
    }

    @Override
    public void save(String token, Long userId, DeviceInfo device, Duration expire) {
        String sessionId = buildKey(token, userId);

        StoredSession sessionMeta = new StoredSession(
                sessionId,
                device.deviceName(),
                device.deviceType(),
                device.userAgent(),
                Instant.now()
        );

        redisStoredSessionTemplate.opsForValue().set(sessionId, sessionMeta, expire);
    }

    @Override
    public boolean isValid(String token, Long userId) {
        return redisStoredSessionTemplate.hasKey(buildKey(token, userId));
    }

    @Override
    public void delete(String token, Long userId) {
        redisStoredSessionTemplate.delete(buildKey(token, userId));
    }

    @Override
    public void deleteBySessionId(String key) {
        redisStoredSessionTemplate.delete(key);
    }

    @Override
    public void deleteAllOf(Long userId, String keepToken) {
        Set<String> keys = redisStoredSessionTemplate.keys("auth::refresh_token:" + userId + ":*");
        if (keys == null || keys.isEmpty()) return;
        String keep = keepToken == null ? null : buildKey(keepToken, userId);
        keys.stream().filter(key -> !key.equals(keep)).forEach(redisStoredSessionTemplate::delete);
    }

    @Override
    public List<SessionInfo> sessionsOf(Long userId, String currentRefreshToken) {
        String keyPattern = "auth::refresh_token:" + userId + ":*";
        Set<String> keys = redisStoredSessionTemplate.keys(keyPattern);

        if (keys == null || keys.isEmpty()) return Collections.emptyList();
        String currentTokenHash = DigestUtils.sha256Hex(currentRefreshToken);

        List<SessionInfo> sessionMetas = new ArrayList<>();
        for (String key : keys) {
            StoredSession meta = redisStoredSessionTemplate.opsForValue().get(key);
            if (meta == null) continue;

            String keyHash = key.substring(key.lastIndexOf(":") + 1);
            boolean isCurrent = currentTokenHash.equals(keyHash);

            SessionInfo sessionMetaResponse = new SessionInfo(
                    meta.getSessionId(),
                    meta.getDeviceName(),
                    meta.getDeviceType(),
                    meta.getUserAgent(),
                    meta.getLoginAt(),
                    isCurrent
            );
            sessionMetas.add(sessionMetaResponse);
        }
        return sessionMetas;
    }

}
