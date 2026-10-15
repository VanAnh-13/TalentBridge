package com.example.talentbridge.auth.infrastructure.refresh;

import com.example.talentbridge.auth.application.port.RefreshTokenStore;
import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.auth.domain.SessionInfo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of RefreshTokenRedisService for development
 * This is used when Redis is not configured (spring.cache.type != redis)
 */
@Service
@Slf4j
@ConditionalOnProperty(
        name = "spring.cache.type",
        havingValue = "simple",
        matchIfMissing = true
)
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    // Map to store session metadata by key
    private final Map<String, StoredSession> sessionStore = new ConcurrentHashMap<>();
    
    // Map to store expiration times
    private final Map<String, Instant> expirationStore = new ConcurrentHashMap<>();

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

        sessionStore.put(sessionId, sessionMeta);
        expirationStore.put(sessionId, Instant.now().plus(expire));
        
        log.debug("Saved refresh token in memory for user: {}", userId);
        
        // Clean up expired tokens
        cleanupExpiredTokens();
    }

    @Override
    public boolean isValid(String token, Long userId) {
        String key = buildKey(token, userId);
        
        // Check if token exists
        if (!sessionStore.containsKey(key)) {
            return false;
        }
        
        // Check if token is expired
        Instant expiration = expirationStore.get(key);
        if (expiration != null && Instant.now().isAfter(expiration)) {
            // Remove expired token
            sessionStore.remove(key);
            expirationStore.remove(key);
            return false;
        }
        
        return true;
    }

    @Override
    public void delete(String token, Long userId) {
        String key = buildKey(token, userId);
        deleteBySessionId(key);
    }

    @Override
    public void deleteBySessionId(String key) {
        sessionStore.remove(key);
        expirationStore.remove(key);
        log.debug("Deleted refresh token: {}", key);
    }

    @Override
    public void deleteAllOf(Long userId, String keepToken) {
        String prefix = "auth::refresh_token:" + userId + ":";
        String keep = keepToken == null ? null : buildKey(keepToken, userId);
        sessionStore.keySet().stream()
                .filter(key -> key.startsWith(prefix) && !key.equals(keep))
                .toList()
                .forEach(this::deleteBySessionId);
    }

    @Override
    public List<SessionInfo> sessionsOf(Long userId, String currentRefreshToken) {
        String keyPrefix = "auth::refresh_token:" + userId + ":";
        String currentTokenHash = DigestUtils.sha256Hex(currentRefreshToken);
        
        cleanupExpiredTokens();
        
        List<SessionInfo> sessionMetas = new ArrayList<>();
        
        for (Map.Entry<String, StoredSession> entry : sessionStore.entrySet()) {
            String key = entry.getKey();
            
            // Check if key belongs to this user
            if (!key.startsWith(keyPrefix)) {
                continue;
            }
            
            StoredSession meta = entry.getValue();
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
    
    /**
     * Clean up expired tokens from memory
     */
    private void cleanupExpiredTokens() {
        Instant now = Instant.now();
        List<String> expiredKeys = new ArrayList<>();
        
        for (Map.Entry<String, Instant> entry : expirationStore.entrySet()) {
            if (now.isAfter(entry.getValue())) {
                expiredKeys.add(entry.getKey());
            }
        }
        
        for (String key : expiredKeys) {
            sessionStore.remove(key);
            expirationStore.remove(key);
        }
        
        if (!expiredKeys.isEmpty()) {
            log.debug("Cleaned up {} expired tokens", expiredKeys.size());
        }
    }
}
