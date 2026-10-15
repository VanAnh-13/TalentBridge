package com.example.talentbridge.resume.application.port;

/** Output port: asks for an application to be (re)scored once its CV is in place. Must never fail the caller. */
public interface ApplicationScoring {

    /** The application has a new or replaced CV; scoring may happen later, after the transaction commits. */
    void cvChanged(Long resumeId);
}
