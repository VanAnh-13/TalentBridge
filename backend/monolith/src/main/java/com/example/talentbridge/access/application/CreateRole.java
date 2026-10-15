package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.access.domain.Permission;
import com.example.talentbridge.access.domain.PermissionRepository;
import com.example.talentbridge.access.domain.Role;
import com.example.talentbridge.access.domain.RoleRepository;

import java.util.HashSet;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRole {

    private final RoleRepository roles;
    private final PermissionRepository permissions;

    public Role execute(String name, String description, List<Long> permissionIds) {
        if (roles.existsByName(name))
            throw new AlreadyExistsException("Chức vụ đã tồn tại");

        List<Permission> found = List.of();
        if (permissionIds != null) {
            found = permissions.findAllById(new HashSet<>(permissionIds));
            if (found.size() != permissionIds.size())
                throw new NotFoundException("Quyền hạn không tồn tại");
        }

        return roles.save(Role.create(name, description, found));
    }
}
