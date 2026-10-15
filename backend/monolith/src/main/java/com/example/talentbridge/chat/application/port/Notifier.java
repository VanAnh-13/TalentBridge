package com.example.talentbridge.chat.application.port;

/**
 * Output port: leaves a notification for someone, unless they are connected right now (they then see the message
 * live). Who is connected is the notification service's knowledge. Must never fail the caller.
 */
public interface Notifier {

    void notifyIfOffline(Long userId, String title, String body, String link);
}
