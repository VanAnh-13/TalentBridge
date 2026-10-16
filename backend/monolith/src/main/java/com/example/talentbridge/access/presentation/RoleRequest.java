package com.example.talentbridge.access.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private boolean active;

    @NotNull(message = "Permissions are required")
    @Valid
    private List<PermissionId> permissions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermissionId {
        @NotNull(message = "Permission id is required")
        private Long id;
    }

    public List<Long> permissionIds() {
        return permissions == null ? null : permissions.stream().map(PermissionId::getId).toList();
    }
}
