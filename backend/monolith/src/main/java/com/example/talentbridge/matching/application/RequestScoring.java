package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.CvMatcher;
import com.example.talentbridge.matching.application.port.ScoringQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Asks for an application to be scored in the background, when scoring is switched on. */
@Service
@RequiredArgsConstructor
public class RequestScoring {

    private final CvMatcher matcher;
    private final ScoringQueue queue;

    public void execute(Long resumeId) {
        if (matcher.available()) queue.enqueue(resumeId);
    }
}
