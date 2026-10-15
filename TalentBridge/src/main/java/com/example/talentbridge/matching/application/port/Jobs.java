package com.example.talentbridge.matching.application.port;

import java.util.List;
import java.util.Optional;

/** Output port towards the job module. */
public interface Jobs {

    record Job(Long id, String name, Long companyId, String companyName, String location, List<String> skills) {
    }

    Optional<Job> find(Long jobId);

    /** The newest jobs that require at least one of the skills. */
    List<Job> latestRequiring(List<String> skillNames, int limit);
}
