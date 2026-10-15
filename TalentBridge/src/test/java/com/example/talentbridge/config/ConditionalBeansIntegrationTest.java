package com.example.talentbridge.config;

import com.example.talentbridge.service.RefreshTokenRedisService;
import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.service.impl.RefreshTokenInMemoryServiceImpl;
import com.example.talentbridge.service.impl.RefreshTokenRedisServiceImpl;
import com.example.talentbridge.shared.infrastructure.storage.LocalFileStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify conditional bean loading based on configuration properties
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.cache.type=simple"
})
@DisplayName("Conditional Beans Integration Tests")
class ConditionalBeansIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RefreshTokenRedisService refreshTokenRedisService;

    @Autowired
    private FileStorage fileStorage;

    @Test
    @DisplayName("Should load in-memory RefreshTokenService when Redis is not configured")
    void testInMemoryRefreshTokenServiceLoaded() {
        // Assert
        assertNotNull(refreshTokenRedisService, "RefreshTokenRedisService bean should be loaded");
        assertInstanceOf(
                RefreshTokenInMemoryServiceImpl.class,
                refreshTokenRedisService,
                "Should load in-memory implementation when Redis is not configured"
        );

        // Verify Redis implementation is NOT loaded
        assertFalse(
                applicationContext.containsBean("refreshTokenRedisServiceImpl"),
                "Redis implementation should not be loaded when cache.type != redis"
        );
    }

    @Test
    @DisplayName("Should load local FileStorage when AWS is not configured")
    void testLocalFileStorageLoaded() {
        // Assert
        assertNotNull(fileStorage, "FileStorage bean should be loaded");
        assertInstanceOf(
                LocalFileStorage.class,
                fileStorage,
                "Should load local implementation when AWS is not configured"
        );

        // Verify AWS implementation is NOT loaded
        assertFalse(
                applicationContext.containsBean("s3ServiceImpl"),
                "AWS S3 implementation should not be loaded when AWS properties are missing"
        );
    }

    @Test
    @DisplayName("Should not load email-related beans when email is not configured")
    void testEmailBeansNotLoaded() {
        // Assert - Email service, controller, and scheduler should not be loaded
        assertFalse(
                applicationContext.containsBean("emailServiceImpl"),
                "EmailService should not be loaded when mail.host is not configured"
        );

        assertFalse(
                applicationContext.containsBean("emailController"),
                "EmailController should not be loaded when mail.host is not configured"
        );

        assertFalse(
                applicationContext.containsBean("jobMailCronService"),
                "JobMailCronService should not be loaded when mail.host is not configured"
        );
    }

    @Test
    @DisplayName("RefreshTokenService should be functional with in-memory implementation")
    void testRefreshTokenServiceFunctionality() {
        // This tests that the injected service actually works
        assertDoesNotThrow(() -> {
            // The service should function normally
            boolean result = refreshTokenRedisService.validateToken("test-token", "test-user");
            assertFalse(result, "Non-existent token should return false");
        }, "RefreshTokenService should be functional");
    }

    @Test
    @DisplayName("FileStorage should be functional with local implementation")
    void testFileStorageFunctionality() {
        // This tests that the injected service actually works
        assertDoesNotThrow(() -> {
            // The service should function normally
            String url = fileStorage.generatePresignedUrl("test/file.txt", java.time.Duration.ofHours(1));
            assertNotNull(url, "Should generate URL even for non-existent file");
        }, "FileStorage should be functional");
    }

    @Test
    @DisplayName("Application should start successfully with development configuration")
    void testApplicationContextLoads() {
        // If we get here, the context loaded successfully
        assertNotNull(applicationContext, "Application context should be loaded");
        
        // Verify essential beans are loaded
        assertTrue(
                applicationContext.containsBean("authServiceImpl"),
                "AuthService should be loaded"
        );
        assertTrue(
                applicationContext.containsBean("userServiceImpl"),
                "UserService should be loaded"
        );
        assertTrue(
                applicationContext.containsBean("createJob"),
                "The job use cases should be loaded"
        );
    }
}
