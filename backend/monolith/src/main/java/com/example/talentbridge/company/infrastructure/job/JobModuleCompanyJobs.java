package com.example.talentbridge.company.infrastructure.job;

import com.example.talentbridge.company.application.port.CompanyJobs;
import com.example.talentbridge.job.application.JobsOfCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapts the job module's application API to the company module's {@link CompanyJobs} port. */
@Component
@RequiredArgsConstructor
public class JobModuleCompanyJobs implements CompanyJobs {

    private final JobsOfCompany jobsOfCompany;

    @Override
    public boolean hasActiveJobs(Long companyId) {
        return jobsOfCompany.hasActive(companyId);
    }

    @Override
    public long countJobs(Long companyId) {
        return jobsOfCompany.count(companyId);
    }

    @Override
    public void deleteAllJobs(Long companyId) {
        jobsOfCompany.deleteAll(companyId);
    }
}
