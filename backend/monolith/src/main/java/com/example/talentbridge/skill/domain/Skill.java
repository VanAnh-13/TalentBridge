package com.example.talentbridge.skill.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;

/** A technical skill a job requires or a subscriber is interested in. Pure domain object. */
public class Skill {

    private final Long id;
    private String name;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Skill(Long id, String name, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = requireName(name);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** A skill that has not been persisted yet. */
    public static Skill create(String name) {
        return new Skill(null, name, null, null);
    }

    /** Rebuilds a persisted skill (used by repository adapters). */
    public static Skill restore(Long id, String name, Instant createdAt, Instant updatedAt) {
        return new Skill(id, name, createdAt, updatedAt);
    }

    public void rename(String newName) {
        this.name = requireName(newName);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidRequestException("Skill name is required");
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
