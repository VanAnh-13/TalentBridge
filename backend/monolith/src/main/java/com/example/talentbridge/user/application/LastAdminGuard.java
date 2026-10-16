package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.user.application.port.RoleCatalog;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Keeps at least one administrator: the last one can be neither deleted nor demoted. */
@Component
@RequiredArgsConstructor
class LastAdminGuard {

    private final UserRepository users;
    private final RoleCatalog roles;

    /** Call before removing {@code user}'s administrator role (or the user itself). */
    void assertNotLastAdmin(User user) {
        Long adminRole = roles.idOfName("ADMIN").orElse(null);
        if (adminRole == null || !adminRole.equals(user.getRoleId()))
            return;
        if (users.findByRoleId(adminRole).size() <= 1)
            throw new BusinessRuleViolationException("The last administrator cannot be deleted or demoted");
    }
}
