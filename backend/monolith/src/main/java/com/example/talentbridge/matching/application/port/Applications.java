package com.example.talentbridge.matching.application.port;

import java.util.List;
import java.util.Optional;

/** Output port towards the resume module. */
public interface Applications {

    record Application(Long resumeId, Long candidateId, Long jobId, Long companyId, String jobName, String jobLevel,
                       String jobLocation, String jobDescription, List<String> jobSkills) {
    }

    record Candidate(Long resumeId, Long candidateId, String email, String status) {
    }

    Optional<Application> find(Long resumeId);

    List<Candidate> candidatesOf(Long jobId);

    Optional<byte[]> cvOf(Long resumeId);
}
