package com.example.talentbridge.notification.infrastructure.mail;

import com.example.talentbridge.notification.application.port.DigestMailer;
import com.example.talentbridge.notification.domain.JobDigest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/** Sends the digest through SMTP; only exists when a mail server host is configured. */
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${spring.mail.host:}' != ''")
public class SmtpDigestMailer implements DigestMailer {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${mail.from:noreply@talentbridge.com}")
    private String sender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void send(JobDigest digest) {
        Context context = new Context();
        context.setVariable("jobs", digest.jobs().stream().map(j -> DigestJobView.of(j, frontendUrl)).toList());
        String html = templateEngine.process("job-notification-email.html", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(digest.recipient());
            helper.setSubject("🔥 Cơ hội việc làm mới dành cho bạn!");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Không thể gửi email: " + e.getMessage(), e);
        }
    }
}
