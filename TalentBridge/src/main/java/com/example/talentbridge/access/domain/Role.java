package com.example.talentbridge.access.domain;

import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A job function (ADMIN, RECRUITER, USER, ...) and the permissions it grants. */
public class Role {

    /** Roles the application relies on: they cannot be renamed or deleted. */
    public static final Set<String> BUILT_IN = Set.of("ADMIN", "RECRUITER", "USER");

    private final Long id;
    private String name;
    private String description;
    private boolean active;
    private List<Permission> permissions;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Role(Long id, String name, String description, boolean active, List<Permission> permissions,
                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = requireText(name, "Tên chức vụ không được để trống");
        this.description = requireText(description, "Mô tả không được để trống");
        this.active = active;
        this.permissions = unique(permissions);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** New roles are always created active. */
    public static Role create(String name, String description, List<Permission> permissions) {
        return new Role(null, name, description, true, permissions, null, null);
    }

    public static Role restore(Long id, String name, String description, boolean active,
                               List<Permission> permissions, Instant createdAt, Instant updatedAt) {
        return new Role(id, name, description, active, permissions, createdAt, updatedAt);
    }

    public boolean isBuiltIn() {
        return name != null && BUILT_IN.contains(name.toUpperCase());
    }

    /** Built-in roles keep their name; description and active flag can always change. */
    public void update(String newName, String newDescription, boolean newActive) {
        if (!isBuiltIn())
            this.name = requireText(newName, "Tên chức vụ không được để trống");
        this.description = requireText(newDescription, "Mô tả không được để trống");
        this.active = newActive;
    }

    public void replacePermissions(List<Permission> newPermissions) {
        this.permissions = unique(newPermissions);
    }

    public void assertDeletable() {
        if (isBuiltIn())
            throw new ForbiddenException("Không thể xóa chức vụ mặc định");
    }

    private static List<Permission> unique(List<Permission> permissions) {
        Map<Object, Permission> byId = new LinkedHashMap<>();
        if (permissions != null)
            for (Permission p : permissions)
                byId.putIfAbsent(p.getId() != null ? p.getId() : p, p);
        return new ArrayList<>(byId.values());
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank())
            throw new InvalidRequestException(message);
        return value;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public List<Permission> getPermissions() { return List.copyOf(permissions); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
