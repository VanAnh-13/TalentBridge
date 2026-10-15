package com.example.talentbridge.access.infrastructure.user;

import com.example.talentbridge.access.application.port.UserRoleDetacher;
import com.example.talentbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Temporary adapter: the user module has not been migrated yet, so users are detached through the legacy
 * repository. It is replaced by a call to the user module's use case when that module moves.
 */
@Component
@RequiredArgsConstructor
public class LegacyUserRoleDetacher implements UserRoleDetacher {

    private final UserRepository userRepository;

    @Override
    public void detachUsersFromRole(Long roleId) {
        userRepository.detachUsersFromRole(roleId);
    }
}
