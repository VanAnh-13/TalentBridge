package com.example.talentbridge.digest.infrastructure.mail;

import com.example.talentbridge.digest.application.port.DigestMailer;
import com.example.talentbridge.digest.domain.JobDigest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/** Stands in for the SMTP mailer when no mail server is configured, so the application still starts. */
@Component
@ConditionalOnExpression("'${spring.mail.host:}' == ''")
public class UnconfiguredDigestMailer implements DigestMailer {

    @Override
    public void send(JobDigest digest) {
        throw new IllegalStateException("Mail is not configured");
    }
}
