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
public class UpdateRole {

    private final RoleRepository roles;
    private final PermissionRepository permissions;

    /** {@code permissionIds == null} leaves the permissions as they are; otherwise they are replaced. */
    public Role execute(Long id, String name, String description, boolean active, List<Long> permissionIds) {
        Role role = roles.findById(id)
                .orElseThrow(() -> new NotFoundException("Chức vụ không tồn tại"));

        if (!role.isBuiltIn() && roles.existsByNameAndIdNot(name, id))
            throw new AlreadyExistsException("Chức vụ đã tồn tại");

        role.update(name, description, active);

        if (permissionIds != null)
            role.replacePermissions(permissions.findAllById(new HashSet<>(permissionIds)));

        return roles.save(role);
    }
}
