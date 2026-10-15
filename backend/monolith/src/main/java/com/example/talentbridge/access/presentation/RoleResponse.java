package com.example.talentbridge.access.presentation;

import com.example.talentbridge.access.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
    private boolean active;
    private String createdAt;
    private String updatedAt;
    private List<PermissionDto> permissions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermissionDto {
        private Long id;
        private String name;
        private String apiPath;
        private String method;
        private String module;
    }

    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive(),
                role.getCreatedAt() == null ? null : role.getCreatedAt().toString(),
                role.getUpdatedAt() == null ? null : role.getUpdatedAt().toString(),
                role.getPermissions().stream()
                        .map(p -> new PermissionDto(p.getId(), p.getName(), p.getApiPath(), p.getMethod(), p.getModule()))
                        .toList());
    }
}
