package com.example.talentbridge.chat.application.port;

/** Output port: leaves a notification for someone who is not online. Must never fail the caller. */
public interface Notifier {

    void notify(Long userId, String title, String body, String link);
}
