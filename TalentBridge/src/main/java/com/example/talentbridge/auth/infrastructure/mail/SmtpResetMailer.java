package com.example.talentbridge.auth.infrastructure.mail;

import com.example.talentbridge.auth.application.port.ResetMailer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Sends the reset link by SMTP. When no mail server is configured nothing is sent, and the link is NOT logged
 * unless {@code app.mail.log-reset-links} is enabled (development only): a logged link lets anyone with
 * access to the logs take over the account.
 */
@Slf4j
@Component
public class SmtpResetMailer implements ResetMailer {

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;     // null when mail is not configured

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${mail.from:noreply@talentbridge.com}")
    private String mailFrom;

    @Value("${app.mail.log-reset-links:false}")
    private boolean logResetLinks;

    public SmtpResetMailer(SpringTemplateEngine templateEngine, @Autowired(required = false) JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordReset(String email, String name, String rawToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;

        if (mailSender == null) {
            if (logResetLinks)
                log.warn("Mail is not configured. Reset link for {}: {}", email, resetLink);
            else
                log.warn("Mail is not configured: the password reset email for an account was not sent");
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink", resetLink);
            context.setVariable("expiryMinutes", 15);
            String html = templateEngine.process("password-reset-email.html", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(email);
            helper.setSubject("🔐 Đặt lại mật khẩu TalentBridge");
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new IllegalStateException("Không thể gửi email. Vui lòng thử lại sau");
        }
    }
}
