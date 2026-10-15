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

    @NotBlank(message = "Tên chức vụ không được để trống")
    private String name;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    private boolean active;

    @NotNull(message = "Danh sách quyền hạn không được để trống")
    @Valid
    private List<PermissionId> permissions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermissionId {
        @NotNull(message = "ID quyền hạn không được để trống")
        private Long id;
    }

    public List<Long> permissionIds() {
        return permissions == null ? null : permissions.stream().map(PermissionId::getId).toList();
    }
}
