package com.example.talentbridge.resume.application.port;

import com.example.talentbridge.resume.domain.ResumeDetails;

/** Output port: tells the people involved in an application what happened. Must never fail the caller. */
public interface ApplicationNotifier {

    /** A candidate applied: the company's recruiters hear about it. */
    void submitted(ResumeDetails application);

    /** A recruiter changed the status: the candidate hears about it. */
    void statusChanged(ResumeDetails application);
}
