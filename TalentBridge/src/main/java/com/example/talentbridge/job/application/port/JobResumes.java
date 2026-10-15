package com.example.talentbridge.job.application.port;

/** Output port towards the resume module. */
public interface JobResumes {

    /** Deletes the resumes submitted to the job together with their files. */
    void deleteAllOf(Long jobId);
}
