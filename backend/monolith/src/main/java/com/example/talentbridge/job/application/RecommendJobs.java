package com.example.talentbridge.job.application;

import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendJobs {

    private final JobRepository jobs;

    /** The newest jobs (at most {@code limit}) that need at least one of the given skills. */
    public List<JobDetails> execute(List<String> skillNames, int limit) {
        return jobs.findLatestBySkillNames(skillNames, limit);
    }
}
