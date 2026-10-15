package com.example.talentbridge.notification.application.port;

import com.example.talentbridge.notification.domain.Notification;

/** Output port: delivers a stored notification to the recipient's open sessions, if any. */
public interface NotificationPublisher {

    void push(Notification notification);
}
