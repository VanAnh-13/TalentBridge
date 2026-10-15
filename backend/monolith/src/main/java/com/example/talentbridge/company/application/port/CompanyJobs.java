package com.example.talentbridge.company.application.port;

/** Output port towards the job module. */
public interface CompanyJobs {

    boolean hasActiveJobs(Long companyId);

    long countJobs(Long companyId);

    /** Deletes every job of the company together with the resumes submitted to them. */
    void deleteAllJobs(Long companyId);
}
