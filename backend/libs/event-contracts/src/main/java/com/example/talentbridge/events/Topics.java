package com.example.talentbridge.events;

/** Kafka topics. The suffix is the major version of what flows through them: a breaking change gets a new topic. */
public final class Topics {

    /** {@link NotificationRequested}: leave a message in someone's inbox (and push it if they are online). */
    public static final String NOTIFICATIONS = "talentbridge.notifications.v1";

    /** {@link RealtimePush}: deliver something live to a user's open browser tabs; nothing is stored. */
    public static final String REALTIME = "talentbridge.realtime.v1";

    private Topics() {
    }
}
