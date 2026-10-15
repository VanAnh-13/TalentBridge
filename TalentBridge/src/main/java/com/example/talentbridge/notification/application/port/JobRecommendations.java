package com.example.talentbridge.notification.application.port;

import com.example.talentbridge.notification.domain.DigestJob;

import java.util.List;

/** Output port towards the job module. */
public interface JobRecommendations {

    /** The newest jobs (at most {@code limit}) that need one of the skills. */
    List<DigestJob> latestFor(List<String> skillNames, int limit);
}
