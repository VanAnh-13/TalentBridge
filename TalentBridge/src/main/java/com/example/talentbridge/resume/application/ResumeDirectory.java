package com.example.talentbridge.resume.application;

import com.example.talentbridge.resume.domain.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** What other modules (interview, chat, matching) may ask about an application. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResumeDirectory {

    /** An application as other modules see it; the CV file is deliberately not part of it. */
    public record Application(Long resumeId, Long candidateId, String candidateEmail, Long jobId, String jobName,
                              Long companyId, String companyName) {
    }

    private final ResumeRepository resumes;

    public Optional<Application> find(Long resumeId) {
        return resumes.findDetailsById(resumeId).map(d -> new Application(
                d.resume().getId(),
                d.user() == null ? d.resume().getUserId() : d.user().id(),
                d.user() == null ? d.resume().getEmail() : d.user().email(),
                d.job() == null ? d.resume().getJobId() : d.job().id(),
                d.job() == null ? null : d.job().name(),
                d.companyId(),
                d.company() == null ? null : d.company().name()));
    }
}
