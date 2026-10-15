package com.example.talentbridge.auth.application;

import java.util.List;
import java.util.Map;

/** What goes into a signed token: the subject, a snapshot of the user, and the permissions of their role. */
public record TokenClaims(String subject, Map<String, Object> user, List<String> permissions) {
}
