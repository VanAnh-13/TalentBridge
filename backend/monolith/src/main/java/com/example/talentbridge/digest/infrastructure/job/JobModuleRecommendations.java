package com.example.talentbridge.digest.infrastructure.job;

import com.example.talentbridge.digest.application.port.JobRecommendations;
import com.example.talentbridge.digest.domain.DigestJob;
import com.example.talentbridge.job.application.RecommendJobs;
import com.example.talentbridge.job.domain.JobDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** Adapts the job module's API to the notification module's {@link JobRecommendations} port. */
@Component
@RequiredArgsConstructor
public class JobModuleRecommendations implements JobRecommendations {

    private final RecommendJobs recommendJobs;

    @Override
    public List<DigestJob> latestFor(List<String> skillNames, int limit) {
        return recommendJobs.execute(skillNames, limit).stream().map(JobModuleRecommendations::toDigestJob).toList();
    }

    private static DigestJob toDigestJob(JobDetails d) {
        DigestJob.Company company = d.company() == null ? null
                : new DigestJob.Company(d.company().id(), d.company().name(), d.company().address());
        return new DigestJob(
                d.job().getId(), d.job().getName(), d.job().getSalary(), company,
                d.skills().stream().map(s -> new DigestJob.Skill(s.id(), s.name())).toList());
    }
}
