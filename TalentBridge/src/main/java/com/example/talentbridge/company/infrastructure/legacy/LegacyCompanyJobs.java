package com.example.talentbridge.company.infrastructure.legacy;

import com.example.talentbridge.company.application.port.CompanyJobs;
import com.example.talentbridge.repository.JobRepository;
import com.example.talentbridge.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Temporary adapter over the legacy job code; replaced when the job module is migrated. */
@Component
@RequiredArgsConstructor
public class LegacyCompanyJobs implements CompanyJobs {

    private final JobRepository jobRepository;
    private final JobService jobService;

    @Override
    public boolean hasActiveJobs(Long companyId) {
        return jobRepository.existsByCompanyIdAndActiveTrue(companyId);
    }

    @Override
    public long countJobs(Long companyId) {
        return jobRepository.countByCompanyId(companyId);
    }

    @Override
    public void deleteAllJobs(Long companyId) {
        jobRepository.findByCompanyId(companyId).forEach(job -> jobService.deleteJobById(job.getId()));
    }
}
