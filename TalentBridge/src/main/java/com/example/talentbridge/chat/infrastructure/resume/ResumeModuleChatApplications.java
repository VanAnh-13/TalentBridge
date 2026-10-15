package com.example.talentbridge.chat.infrastructure.resume;

import com.example.talentbridge.chat.application.port.Applications;
import com.example.talentbridge.resume.application.ResumeDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResumeModuleChatApplications implements Applications {

    private final ResumeDirectory resumes;

    @Override
    public Optional<Application> find(Long resumeId) {
        return resumes.find(resumeId).map(a -> new Application(a.resumeId(), a.candidateId(), a.candidateEmail(),
                a.jobName(), a.companyId(), a.companyName()));
    }
}
