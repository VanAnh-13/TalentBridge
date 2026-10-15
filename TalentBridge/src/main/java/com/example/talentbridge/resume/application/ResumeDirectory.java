package com.example.talentbridge.resume.application;

import com.example.talentbridge.resume.domain.ResumeRepository;
import com.example.talentbridge.resume.domain.ResumeStatus;
import com.example.talentbridge.shared.application.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** What other modules (interview, chat, matching) may ask about an application. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResumeDirectory {

    /** An application as other modules see it; the CV file is deliberately not part of it. */
    public record Application(Long resumeId, Long candidateId, String candidateEmail, Long jobId, String jobName,
                              Long companyId, String companyName, ResumeStatus status, String jobLevel,
                              String jobLocation, String jobDescription, List<String> jobSkills) {
    }

    /** One entry of the list of applications to a job. */
    public record Candidate(Long resumeId, Long candidateId, String email, ResumeStatus status) {
    }

    private final ResumeRepository resumes;
    private final FileStorage storage;

    public Optional<Application> find(Long resumeId) {
        return resumes.findDetailsById(resumeId).map(d -> new Application(
                d.resume().getId(),
                d.user() == null ? d.resume().getUserId() : d.user().id(),
                d.user() == null ? d.resume().getEmail() : d.user().email(),
                d.job() == null ? d.resume().getJobId() : d.job().id(),
                d.job() == null ? null : d.job().name(),
                d.companyId(),
                d.company() == null ? null : d.company().name(),
                d.resume().getStatus(),
                d.job() == null ? null : d.job().level(),
                d.job() == null ? null : d.job().location(),
                d.job() == null ? null : d.job().description(),
                d.job() == null || d.job().skills() == null ? List.of() : d.job().skills()));
    }

    public List<Candidate> candidatesOf(Long jobId) {
        return resumes.findByJobId(jobId).stream()
                .map(r -> new Candidate(r.getId(), r.getUserId(), r.getEmail(), r.getStatus()))
                .toList();
    }

    /** The CV file (PDF) of an application, if one is attached and readable. */
    public Optional<byte[]> cvOf(Long resumeId) {
        return resumes.findDetailsById(resumeId)
                .map(d -> d.resume().getFileKey())
                .filter(key -> key != null && !key.isBlank())
                .map(storage::read);
    }
}
