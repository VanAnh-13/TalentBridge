package com.example.talentbridge.matching.infrastructure.job;

import com.example.talentbridge.job.application.GetJob;
import com.example.talentbridge.job.application.RecommendJobs;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.matching.application.port.Jobs;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobModuleJobs implements Jobs {

    private final GetJob getJob;
    private final RecommendJobs recommendJobs;

    @Override
    public Optional<Job> find(Long jobId) {
        try {
            return Optional.of(toJob(getJob.execute(jobId)));
        } catch (NotFoundException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Job> latestRequiring(List<String> skillNames, int limit) {
        return recommendJobs.execute(skillNames, limit).stream().map(JobModuleJobs::toJob).toList();
    }

    private static Job toJob(JobDetails d) {
        return new Job(d.job().getId(), d.job().getName(), d.company() == null ? null : d.company().id(),
                d.company() == null ? null : d.company().name(), d.job().getLocation(),
                d.skills().stream().map(JobDetails.SkillSummary::name).toList());
    }
}
