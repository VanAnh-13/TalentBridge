package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.access.domain.Permission;
import com.example.talentbridge.access.domain.Role;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.access.domain.PermissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeletePermission {

    private final PermissionRepository permissions;

    public Permission execute(Long id) {
        Permission permission = permissions.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền hạn này"));

        if (Role.ADMIN_ESSENTIALS.contains(permission.authority()))
            throw new ForbiddenException("Không thể xóa quyền hạn mà chức vụ ADMIN cần để quản trị hệ thống");

        permissions.delete(permission);
        return permission;
    }
}
