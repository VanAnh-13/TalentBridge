package com.example.talentbridge.access.presentation;

import com.example.talentbridge.access.domain.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionResponse {

    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private String createdAt;
    private String updatedAt;

    public static PermissionResponse from(Permission p) {
        return new PermissionResponse(
                p.getId(), p.getName(), p.getApiPath(), p.getMethod(), p.getModule(),
                p.getCreatedAt() == null ? null : p.getCreatedAt().toString(),
                p.getUpdatedAt() == null ? null : p.getUpdatedAt().toString());
    }
}
