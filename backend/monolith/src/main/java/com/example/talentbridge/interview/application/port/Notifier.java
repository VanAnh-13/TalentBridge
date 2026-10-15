package com.example.talentbridge.interview.application.port;

/** Output port: tells one user something about an interview. Must never fail the caller. */
public interface Notifier {

    void notify(Long userId, String type, String title, String body, String link);
}
