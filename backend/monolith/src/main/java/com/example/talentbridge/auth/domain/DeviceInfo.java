package com.example.talentbridge.auth.domain;

/** The device a session was opened from. */
public record DeviceInfo(String deviceName, String deviceType, String userAgent) {
}
