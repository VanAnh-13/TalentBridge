package com.example.talentbridge.service.impl;

import com.example.talentbridge.dto.response.auth.TokenValidationResponse;
import com.example.talentbridge.model.PasswordResetToken;
import com.example.talentbridge.user.infrastructure.persistence.UserJpaEntity;
import com.example.talentbridge.repository.PasswordResetTokenRepository;
import com.example.talentbridge.user.infrastructure.persistence.UserJpaRepository;
import com.example.talentbridge.service.PasswordResetService;
import com.example.talentbridge.service.TokenService;
import com.example.talentbridge.util.PasswordValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserJpaRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final SpringTemplateEngine templateEngine;
    
    // Optional - may be null if mail is not configured
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${mail.from:noreply@talentbridge.com}")
    private String mailFrom;

    @Autowired
    public PasswordResetServiceImpl(
            UserJpaRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            SpringTemplateEngine templateEngine,
            @Autowired(required = false) JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    private static final int RATE_LIMIT_MINUTES = 1;

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // Check rate limiting
        Instant rateLimitSince = Instant.now().minus(RATE_LIMIT_MINUTES, ChronoUnit.MINUTES);
        long recentRequests = tokenRepository.countRecentRequestsByEmail(email, rateLimitSince);

        if (recentRequests > 0) {
            log.warn("Rate limit exceeded for email: {}", email);
            // Still return success to prevent email enumeration
            return;
        }

        Optional<UserJpaEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Don't reveal that email doesn't exist - just log and return
            log.info("Password reset requested for non-existent email: {}", email);
            return;
        }

        UserJpaEntity user = userOpt.get();

        // Generate token
        String rawToken = tokenService.generateToken();
        String hashedToken = tokenService.hashToken(rawToken);

        // Create and save token entity
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(hashedToken)
                .user(user)
                .expiryDate(tokenService.calculateExpiryDate())
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Send email if mail service is configured
        if (mailSender != null) {
            try {
                sendPasswordResetEmail(user.getEmail(), user.getName(), rawToken);
            } catch (MessagingException e) {
                log.error("Failed to send password reset email to: {}", email, e);
                throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau");
            }
        } else {
            log.warn("Mail service not configured. Reset token for {}: {}", email, rawToken);
            log.info("Reset link would be: {}/reset-password?token={}", frontendUrl, rawToken);
        }
    }

    @Override
    public TokenValidationResponse validateResetToken(String token) {
        String hashedToken = tokenService.hashToken(token);
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(hashedToken);

        if (tokenOpt.isEmpty()) {
            return TokenValidationResponse.notFound();
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed()) {
            return TokenValidationResponse.used();
        }

        if (resetToken.isExpired()) {
            return TokenValidationResponse.expired();
        }

        return TokenValidationResponse.valid();
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        // Validate password
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(newPassword);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(String.join(", ", validationResult.getErrors()));
        }

        // Validate password match
        if (!PasswordValidator.validatePasswordMatch(newPassword, confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        // Validate token
        String hashedToken = tokenService.hashToken(token);
        PasswordResetToken resetToken = tokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("Liên kết không hợp lệ"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Liên kết đã được sử dụng");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Liên kết đã hết hạn. Vui lòng yêu cầu liên kết mới");
        }

        // Update password
        UserJpaEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate all tokens for this user
        tokenRepository.invalidateAllTokensForUser(user.getId());

        log.info("Password reset successful for user: {}", user.getEmail());
    }

    private void sendPasswordResetEmail(String email, String name, String token) throws MessagingException {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetLink", resetLink);
        context.setVariable("expiryMinutes", 15);

        String html = templateEngine.process("password-reset-email.html", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(mailFrom);
        helper.setTo(email);
        helper.setSubject("🔐 Đặt lại mật khẩu TalentBridge");
        helper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("Password reset email sent to: {}", email);
    }
}
