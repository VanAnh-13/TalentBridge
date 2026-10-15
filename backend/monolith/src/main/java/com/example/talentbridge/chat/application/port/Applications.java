package com.example.talentbridge.chat.application.port;

import java.util.Optional;

/** Output port towards the resume module. */
public interface Applications {

    record Application(Long resumeId, Long candidateId, String candidateEmail, String jobName, Long companyId,
                       String companyName) {
    }

    Optional<Application> find(Long resumeId);
}
