package com.example.talentbridge.matching.application.port;

/** Output port: runs a scoring in the background, once the surrounding transaction (if any) has committed. */
public interface ScoringQueue {

    void enqueue(Long resumeId);
}
