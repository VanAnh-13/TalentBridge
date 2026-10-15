package com.example.talentbridge.notification.application.port;

import com.example.talentbridge.notification.domain.JobDigest;

/** Output port: delivers a digest to its recipient. */
public interface DigestMailer {

    void send(JobDigest digest);
}
