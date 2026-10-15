package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.access.application.port.UserRoleDetacher;
import com.example.talentbridge.access.domain.Role;
import com.example.talentbridge.access.domain.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteRole {

    private final RoleRepository roles;
    private final UserRoleDetacher userRoleDetacher;

    public Role execute(Long id) {
        Role role = roles.findById(id)
                .orElseThrow(() -> new NotFoundException("Chức vụ không tồn tại"));

        role.assertDeletable();

        userRoleDetacher.detachUsersFromRole(role.getId());
        roles.delete(role);
        return role;
    }
}
