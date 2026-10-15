package com.example.talentbridge.auth.infrastructure.refresh;

import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.auth.domain.SessionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryRefreshTokenStore Unit Tests")
class InMemoryRefreshTokenStoreTest {

    private InMemoryRefreshTokenStore refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new InMemoryRefreshTokenStore();
    }

    @Test
    @DisplayName("Should save and validate refresh token successfully")
    void testSaveAndValidateToken() {
        // Arrange
        String token = "test-refresh-token-123";
        Long userId = 1L;
        DeviceInfo sessionMetaRequest = new DeviceInfo(
                "Chrome Browser",
                "Desktop",
                "Mozilla/5.0"
        );
        Duration expire = Duration.ofMinutes(30);

        // Act
        refreshTokenService.save(token, userId, sessionMetaRequest, expire);
        boolean isValid = refreshTokenService.isValid(token, userId);

        // Assert
        assertTrue(isValid, "Token should be valid after saving");
    }

    @Test
    @DisplayName("Should return false for non-existent token")
    void testValidateNonExistentToken() {
        // Arrange
        String token = "non-existent-token";
        Long userId = 2L;

        // Act
        boolean isValid = refreshTokenService.isValid(token, userId);

        // Assert
        assertFalse(isValid, "Non-existent token should be invalid");
    }

    @Test
    @DisplayName("Should delete refresh token successfully")
    void testDeleteRefreshToken() {
        // Arrange
        String token = "test-refresh-token-to-delete";
        Long userId = 3L;
        DeviceInfo sessionMetaRequest = new DeviceInfo(
                "Safari Browser",
                "Mobile",
                "Safari/537.36"
        );
        Duration expire = Duration.ofMinutes(30);

        // Act
        refreshTokenService.save(token, userId, sessionMetaRequest, expire);
        assertTrue(refreshTokenService.isValid(token, userId), "Token should exist before deletion");

        refreshTokenService.delete(token, userId);
        boolean isValidAfterDelete = refreshTokenService.isValid(token, userId);

        // Assert
        assertFalse(isValidAfterDelete, "Token should be invalid after deletion");
    }

    @Test
    @DisplayName("Should return all session metas for a user")
    void testGetAllSessionMetas() {
        // Arrange
        Long userId = 4L;
        String token1 = "token-session-1";
        String token2 = "token-session-2";
        String currentToken = token1;

        DeviceInfo session1 = new DeviceInfo("Chrome", "Desktop", "Chrome/90.0");
        DeviceInfo session2 = new DeviceInfo("Firefox", "Desktop", "Firefox/88.0");

        // Act
        refreshTokenService.save(token1, userId, session1, Duration.ofMinutes(30));
        refreshTokenService.save(token2, userId, session2, Duration.ofMinutes(30));

        List<SessionInfo> sessionMetas = refreshTokenService.sessionsOf(userId, currentToken);

        // Assert
        assertNotNull(sessionMetas, "Session metas list should not be null");
        assertEquals(2, sessionMetas.size(), "Should return 2 session metas");
        
        // Verify current session is marked correctly
        long currentSessionCount = sessionMetas.stream().filter(SessionInfo::current).count();
        assertEquals(1, currentSessionCount, "Exactly one session should be marked as current");
    }

    @Test
    @DisplayName("Should return empty list when no sessions exist for user")
    void testGetAllSessionMetasEmptyList() {
        // Arrange
        Long userId = 5L;
        String currentToken = "some-token";

        // Act
        List<SessionInfo> sessionMetas = refreshTokenService.sessionsOf(userId, currentToken);

        // Assert
        assertNotNull(sessionMetas, "Session metas list should not be null");
        assertTrue(sessionMetas.isEmpty(), "Session metas list should be empty");
    }

    @Test
    @DisplayName("Should delete token by session key")
    void testDeleteRefreshTokenByKey() {
        // Arrange
        String token = "test-token-delete-by-key";
        Long userId = 6L;
        DeviceInfo sessionMetaRequest = new DeviceInfo(
                "Edge Browser",
                "Desktop",
                "Edge/90.0"
        );
        Duration expire = Duration.ofMinutes(30);

        // Act
        refreshTokenService.save(token, userId, sessionMetaRequest, expire);
        
        // Get all sessions to find the session key
        List<SessionInfo> sessions = refreshTokenService.sessionsOf(userId, token);
        String sessionKey = sessions.get(0).sessionId();

        refreshTokenService.deleteBySessionId(sessionKey);
        boolean isValidAfterDelete = refreshTokenService.isValid(token, userId);

        // Assert
        assertFalse(isValidAfterDelete, "Token should be invalid after deletion by key");
    }

    @Test
    @DisplayName("Should handle multiple users independently")
    void testMultipleUsersIndependently() {
        // Arrange
        Long user1Id = 7L;
        Long user2Id = 8L;
        String token = "same-token-different-users";
        
        DeviceInfo session1 = new DeviceInfo("Chrome", "Desktop", "Chrome/90.0");
        DeviceInfo session2 = new DeviceInfo("Firefox", "Mobile", "Firefox/88.0");

        // Act
        refreshTokenService.save(token, user1Id, session1, Duration.ofMinutes(30));
        refreshTokenService.save(token, user2Id, session2, Duration.ofMinutes(30));

        boolean user1Valid = refreshTokenService.isValid(token, user1Id);
        boolean user2Valid = refreshTokenService.isValid(token, user2Id);

        // Delete user1's token
        refreshTokenService.delete(token, user1Id);

        boolean user1ValidAfterDelete = refreshTokenService.isValid(token, user1Id);
        boolean user2ValidAfterDelete = refreshTokenService.isValid(token, user2Id);

        // Assert
        assertTrue(user1Valid, "User 1 token should be valid initially");
        assertTrue(user2Valid, "User 2 token should be valid initially");
        assertFalse(user1ValidAfterDelete, "User 1 token should be invalid after deletion");
        assertTrue(user2ValidAfterDelete, "User 2 token should still be valid after user 1's deletion");
    }

    @Test
    @DisplayName("Should return correct session metadata")
    void testSessionMetadataContent() {
        // Arrange
        String token = "metadata-test-token";
        Long userId = 9L;
        String deviceName = "MacBook Pro";
        String deviceType = "Desktop";
        String userAgent = "Mozilla/5.0 (Macintosh)";

        DeviceInfo sessionMetaRequest = new DeviceInfo(deviceName, deviceType, userAgent);

        // Act
        refreshTokenService.save(token, userId, sessionMetaRequest, Duration.ofMinutes(30));
        List<SessionInfo> sessionMetas = refreshTokenService.sessionsOf(userId, token);

        // Assert
        assertFalse(sessionMetas.isEmpty(), "Should have at least one session");
        SessionInfo session = sessionMetas.get(0);
        assertEquals(deviceName, session.deviceName(), "Device name should match");
        assertEquals(deviceType, session.deviceType(), "Device type should match");
        assertEquals(userAgent, session.userAgent(), "User agent should match");
        assertNotNull(session.loginAt(), "Login timestamp should not be null");
        assertTrue(session.current(), "Should be marked as current session");
    }

    @Test
    @DisplayName("Should handle expired tokens correctly")
    void testExpiredTokens() throws InterruptedException {
        // Arrange
        String token = "expiring-token";
        Long userId = 10L;
        DeviceInfo sessionMetaRequest = new DeviceInfo(
                "Test Browser",
                "Desktop",
                "TestAgent/1.0"
        );
        Duration expire = Duration.ofMillis(100); // Very short expiration

        // Act
        refreshTokenService.save(token, userId, sessionMetaRequest, expire);
        assertTrue(refreshTokenService.isValid(token, userId), "Token should be valid immediately");

        // Wait for expiration
        Thread.sleep(200);

        boolean isValidAfterExpiry = refreshTokenService.isValid(token, userId);

        // Assert
        assertFalse(isValidAfterExpiry, "Token should be invalid after expiration");
    }

    @Test
    @DisplayName("deleteAllOf removes every session of the user except the kept one, and nobody else's")
    void testDeleteAllOf() {
        DeviceInfo device = new DeviceInfo("d", "web", "ua");
        refreshTokenService.save("a", 7L, device, Duration.ofMinutes(5));
        refreshTokenService.save("b", 7L, device, Duration.ofMinutes(5));
        refreshTokenService.save("c", 8L, device, Duration.ofMinutes(5));

        refreshTokenService.deleteAllOf(7L, "a");

        assertTrue(refreshTokenService.isValid("a", 7L));
        assertFalse(refreshTokenService.isValid("b", 7L));
        assertTrue(refreshTokenService.isValid("c", 8L));

        refreshTokenService.deleteAllOf(7L, null);
        assertFalse(refreshTokenService.isValid("a", 7L));
    }
}
