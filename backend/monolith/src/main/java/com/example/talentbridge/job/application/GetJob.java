package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetJob {

    private final JobRepository jobs;

    public JobDetails execute(Long id) {
        return jobs.findDetailsById(id).orElseThrow(() -> new NotFoundException("Job not found"));
    }
}
