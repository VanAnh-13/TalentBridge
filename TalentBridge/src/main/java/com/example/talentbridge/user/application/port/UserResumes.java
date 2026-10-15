package com.example.talentbridge.user.application.port;

/** Output port towards the resume module. */
public interface UserResumes {

    void deleteAllOf(Long userId);
}
