package com.example.talentbridge.job.application.port;

/** Output port towards the company module. */
public interface CompanyDirectory {

    boolean exists(Long companyId);
}
