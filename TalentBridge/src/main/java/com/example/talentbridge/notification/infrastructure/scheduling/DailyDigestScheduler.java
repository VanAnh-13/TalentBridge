package com.example.talentbridge.notification.infrastructure.scheduling;

import com.example.talentbridge.notification.application.SendJobDigest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Every day at 08:00 server time. Only runs when a mail server is configured. */
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${spring.mail.host:}' != ''")
public class DailyDigestScheduler {

    private final SendJobDigest sendJobDigest;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyDigest() {
        sendJobDigest.toAllSubscribers();
    }
}
