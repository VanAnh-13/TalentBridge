package com.example.talentbridge.access.infrastructure.user;

import com.example.talentbridge.access.application.port.UserRoleDetacher;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapts the user module's API to the access module's {@link UserRoleDetacher} port. */
@Component
@RequiredArgsConstructor
public class UserModuleRoleDetacher implements UserRoleDetacher {

    private final UserDirectory users;

    @Override
    public void detachUsersFromRole(Long roleId) {
        users.detachRole(roleId);
    }
}
