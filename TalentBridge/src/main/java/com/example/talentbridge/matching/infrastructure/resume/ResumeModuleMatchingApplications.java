package com.example.talentbridge.matching.infrastructure.resume;

import com.example.talentbridge.matching.application.port.Applications;
import com.example.talentbridge.resume.application.ResumeDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResumeModuleMatchingApplications implements Applications {

    private final ResumeDirectory resumes;

    @Override
    public Optional<Application> find(Long resumeId) {
        return resumes.find(resumeId).map(a -> new Application(a.resumeId(), a.candidateId(), a.jobId(), a.companyId(),
                a.jobName(), a.jobLevel(), a.jobLocation(), a.jobDescription(), a.jobSkills()));
    }

    @Override
    public List<Candidate> candidatesOf(Long jobId) {
        return resumes.candidatesOf(jobId).stream()
                .map(c -> new Candidate(c.resumeId(), c.candidateId(), c.email(), c.status().name())).toList();
    }

    @Override
    public Optional<byte[]> cvOf(Long resumeId) {
        return resumes.cvOf(resumeId);
    }
}
