package com.example.talentbridge.skill.application.port;

/** Output port: who else refers to a skill. Implemented where the shared tables are known. */
public interface SkillUsage {

    /** How many jobs require the skill. */
    long jobsRequiring(Long skillId);

    /** Stops every subscriber from following the skill; the subscribers themselves are kept. */
    void detachFromSubscribers(Long skillId);
}
