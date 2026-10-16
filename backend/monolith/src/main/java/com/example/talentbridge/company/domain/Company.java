package com.example.talentbridge.company.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;
import java.util.Objects;

/** An employer that posts jobs. Members and jobs are referenced by id, never held. */
public class Company {

    private final Long id;
    private String name;
    private String description;
    private String address;
    private String logoUrl;
    private Long ownerId;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Company(Long id, String name, String description, String address, String logoUrl, Long ownerId,
                    Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.logoUrl = logoUrl;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        describe(name, description, address);
    }

    public static Company create(String name, String description, String address) {
        return new Company(null, name, description, address, null, null, null, null);
    }

    public static Company restore(Long id, String name, String description, String address, String logoUrl,
                                  Long ownerId, Instant createdAt, Instant updatedAt) {
        return new Company(id, name, description, address, logoUrl, ownerId, createdAt, updatedAt);
    }

    public void update(String name, String description, String address) {
        describe(name, description, address);
    }

    public void changeLogo(String url) {
        this.logoUrl = url;
    }

    public void ownedBy(Long userId) {
        this.ownerId = userId;
    }

    public boolean isOwnedBy(Long userId) {
        return ownerId != null && Objects.equals(ownerId, userId);
    }

    private void describe(String name, String description, String address) {
        if (name == null || name.isBlank())
            throw new InvalidRequestException("Company name is required");
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getLogoUrl() { return logoUrl; }
    public Long getOwnerId() { return ownerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
