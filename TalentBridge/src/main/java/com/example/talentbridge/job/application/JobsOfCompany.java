package com.example.talentbridge.job.application;

import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** What other modules (company) need to know about, or do to, the jobs of a company. */
@Service
@Transactional
@RequiredArgsConstructor
public class JobsOfCompany {

    private final JobRepository jobs;
    private final DeleteJob deleteJob;

    @Transactional(readOnly = true)
    public long count(Long companyId) {
        return jobs.countByCompany(companyId);
    }

    @Transactional(readOnly = true)
    public boolean hasActive(Long companyId) {
        return jobs.existsActiveByCompany(companyId);
    }

    public void deleteAll(Long companyId) {
        jobs.findIdsByCompany(companyId).forEach(deleteJob::execute);
    }
}
