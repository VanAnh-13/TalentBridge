package com.example.talentbridge.realtime.application.port;

/** Output port: whether a user has an open live connection right now. */
public interface Presence {

    boolean isOnline(Long userId);
}
