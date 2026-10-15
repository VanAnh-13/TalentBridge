package com.example.talentbridge.events;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.regex.Pattern;

/**
 * Deliver {@code data} live on {@code /user/queue/<channel>} to every open session of the user. Nothing is stored: a
 * user who is not connected simply does not get it.
 */
public record RealtimePush(long userId, String channel, JsonNode data) {

    public static final String TYPE = "RealtimePush";
    public static final int VERSION = 1;

    /** Channel names that can safely become part of a STOMP destination. */
    public static final Pattern CHANNEL = Pattern.compile("[a-z][a-z0-9-]{0,39}");
}
