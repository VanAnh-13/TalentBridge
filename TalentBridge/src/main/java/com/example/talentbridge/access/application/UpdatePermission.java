package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.access.domain.Permission;
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
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền hạn này"));

        if (permissions.existsByApiPathAndMethodAndIdNot(apiPath, method, id))
            throw new AlreadyExistsException("Quyền hạn này đã tồn tại");

        permission.redefine(name, apiPath, method, module);
        return permissions.save(permission);
    }
}
