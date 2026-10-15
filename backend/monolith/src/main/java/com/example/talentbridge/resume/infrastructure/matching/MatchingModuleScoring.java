package com.example.talentbridge.resume.infrastructure.matching;

import com.example.talentbridge.matching.application.RequestScoring;
import com.example.talentbridge.resume.application.port.ApplicationScoring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Adapter: scoring belongs to the matching module. A failure is logged, never propagated. */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingModuleScoring implements ApplicationScoring {

    private final RequestScoring requestScoring;

    @Override
    public void cvChanged(Long resumeId) {
        try {
            requestScoring.execute(resumeId);
        } catch (RuntimeException ex) {
            log.warn("Could not request scoring of application {}: {}", resumeId, ex.getMessage());
        }
    }
}
