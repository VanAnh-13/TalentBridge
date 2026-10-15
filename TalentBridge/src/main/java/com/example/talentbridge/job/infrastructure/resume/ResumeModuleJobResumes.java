package com.example.talentbridge.job.infrastructure.resume;

import com.example.talentbridge.job.application.port.JobResumes;
import com.example.talentbridge.resume.application.ResumesOf;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapts the resume module's application API to the job module's {@link JobResumes} port. */
@Component
@RequiredArgsConstructor
public class ResumeModuleJobResumes implements JobResumes {

    private final ResumesOf resumesOf;

    @Override
    public void deleteAllOf(Long jobId) {
        resumesOf.deleteAllOfJob(jobId);
    }
}
