package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.access.domain.Permission;
import com.example.talentbridge.access.domain.PermissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePermission {

    private final PermissionRepository permissions;

    public Permission execute(String name, String apiPath, String method, String module) {
        if (permissions.existsByApiPathAndMethod(apiPath, method))
            throw new AlreadyExistsException("This permission already exists");

        return permissions.save(Permission.create(name, apiPath, method, module));
    }
}
