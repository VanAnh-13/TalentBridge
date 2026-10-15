package com.example.talentbridge.events;

/**
 * Put a message in a user's inbox. With {@code onlyIfOffline}, skip it when the user is connected right now (they
 * already see the thing live, e.g. a chat message).
 */
public record NotificationRequested(long userId, String type, String title, String body, String link,
                                    boolean onlyIfOffline) {

    public static final String TYPE = "NotificationRequested";
    public static final int VERSION = 1;
}
