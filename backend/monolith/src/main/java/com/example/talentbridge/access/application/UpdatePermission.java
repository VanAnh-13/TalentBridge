package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
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
public class UpdatePermission {

    private final PermissionRepository permissions;

    public Permission execute(Long id, String name, String apiPath, String method, String module) {
        Permission permission = permissions.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission not found"));

        if (permissions.existsByApiPathAndMethodAndIdNot(apiPath, method, id))
            throw new AlreadyExistsException("This permission already exists");

        boolean essential = Role.ADMIN_ESSENTIALS.contains(permission.authority());
        permission.redefine(name, apiPath, method, module);
        if (essential && !Role.ADMIN_ESSENTIALS.contains(permission.authority()))
            throw new ForbiddenException("The method/path of this permission cannot change: the ADMIN role needs it to administer the system");

        return permissions.save(permission);
    }
}
