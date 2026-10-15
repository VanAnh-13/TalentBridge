package com.example.talentbridge.realtime.application.port;

/** Output port: delivers a payload to the open sessions of one user, on {@code /user/queue/<channel>}. */
public interface LivePusher {

    void push(long userId, String channel, Object payload);
}
