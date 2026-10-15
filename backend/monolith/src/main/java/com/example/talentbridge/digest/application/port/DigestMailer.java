package com.example.talentbridge.digest.application.port;

import com.example.talentbridge.digest.domain.JobDigest;

/** Output port: delivers a digest to its recipient. */
public interface DigestMailer {

    void send(JobDigest digest);
}
