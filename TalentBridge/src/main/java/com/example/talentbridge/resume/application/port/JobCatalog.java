package com.example.talentbridge.resume.application.port;

/** Output port towards the job module. */
public interface JobCatalog {

    boolean exists(Long jobId);
}
