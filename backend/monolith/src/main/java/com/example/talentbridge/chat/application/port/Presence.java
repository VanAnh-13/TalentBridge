package com.example.talentbridge.chat.application.port;

/** Output port: who has a live connection right now. */
public interface Presence {

    boolean isOnline(Long userId);
}
