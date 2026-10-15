package com.example.talentbridge.access.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;

/** The right to call one API endpoint: an HTTP method plus a path, grouped by module. */
public class Permission {

    private final Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Permission(Long id, String name, String apiPath, String method, String module,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        define(name, apiPath, method, module);
    }

    public static Permission create(String name, String apiPath, String method, String module) {
        return new Permission(null, name, apiPath, method, module, null, null);
    }

    public static Permission restore(Long id, String name, String apiPath, String method, String module,
                                     Instant createdAt, Instant updatedAt) {
        return new Permission(id, name, apiPath, method, module, createdAt, updatedAt);
    }

    public void redefine(String name, String apiPath, String method, String module) {
        define(name, apiPath, method, module);
    }

    private void define(String name, String apiPath, String method, String module) {
        this.name = requireText(name, "Tên không được để trống");
        this.apiPath = requireText(apiPath, "Đường dẫn không được để trống");
        this.method = requireText(method, "Phương thức không được để trống");
        this.module = requireText(module, "Module không được để trống");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank())
            throw new InvalidRequestException(message);
        return value;
    }

    /** The authority string carried in tokens and checked by {@code @PreAuthorize}: "METHOD /path". */
    public String authority() {
        return method + " " + apiPath;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getApiPath() { return apiPath; }
    public String getMethod() { return method; }
    public String getModule() { return module; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
