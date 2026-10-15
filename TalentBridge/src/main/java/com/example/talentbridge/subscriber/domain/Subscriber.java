package com.example.talentbridge.subscriber.domain;

import java.util.List;

/** A person who receives job recommendation emails for the skills they chose. */
public class Subscriber {

    private final Long id;
    private final String email;
    private List<SubscribedSkill> skills;

    private Subscriber(Long id, String email, List<SubscribedSkill> skills) {
        this.id = id;
        this.email = email;
        this.skills = skills == null ? List.of() : List.copyOf(skills);
    }

    public static Subscriber create(String email, List<SubscribedSkill> skills) {
        return new Subscriber(null, email, skills);
    }

    public static Subscriber restore(Long id, String email, List<SubscribedSkill> skills) {
        return new Subscriber(id, email, skills);
    }

    /** Replaces the whole set of subscribed skills. */
    public void changeSkills(List<SubscribedSkill> newSkills) {
        this.skills = newSkills == null ? List.of() : List.copyOf(newSkills);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public List<SubscribedSkill> getSkills() {
        return skills;
    }
}
