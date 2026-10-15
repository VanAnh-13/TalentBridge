package com.example.talentbridge.interview.application.port;

import java.util.Optional;

/** Output port towards the resume module: the application an interview is about. */
public interface Applications {

    record Application(Long resumeId, Long candidateId, String candidateEmail, String jobName, Long companyId,
                       String companyName) {
    }

    Optional<Application> find(Long resumeId);
}
