package com.example.talentbridge.interview.domain;

public enum InterviewStatus {
    /** The recruiter offered time slots; the candidate has not answered. */
    PROPOSED,
    /** The candidate picked a slot. */
    CONFIRMED,
    /** The candidate turned the interview down. */
    DECLINED,
    /** The recruiter called it off. */
    CANCELLED
}
