package com.example.talentbridge.user.domain;

/** Read model: a user with the display data of their company and role. */
public record UserDetails(User user, CompanySummary company, RoleSummary role) {

    public record CompanySummary(Long id, String name, String address, String logoUrl) {
    }

    public record RoleSummary(Long id, String name, String description) {
    }
}
