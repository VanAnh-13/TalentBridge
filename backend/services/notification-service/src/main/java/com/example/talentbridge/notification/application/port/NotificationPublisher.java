package com.example.talentbridge.notification.application.port;

import com.example.talentbridge.notification.domain.Notification;

/** Output port: shows a stored notification live to its owner if they are connected. Must never fail the caller. */
public interface NotificationPublisher {

    void push(Notification notification);
}
