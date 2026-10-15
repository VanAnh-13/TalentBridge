package com.example.talentbridge.notification.domain;

import java.util.List;

/** A job as it appears in a digest email. */
public record DigestJob(Long id, String name, Double salary, Company company, List<Skill> skills) {

    public record Company(Long id, String name, String address) {
    }

    public record Skill(Long id, String name) {
    }
}
