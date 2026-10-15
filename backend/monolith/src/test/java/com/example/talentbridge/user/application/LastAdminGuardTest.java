package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.user.application.port.RoleCatalog;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LastAdminGuardTest {

    private static final long ADMIN_ROLE = 1L, OTHER_ROLE = 2L;

    private final UserRepository users = mock(UserRepository.class);
    private final RoleCatalog roles = mock(RoleCatalog.class);
    private final LastAdminGuard guard = new LastAdminGuard(users, roles);

    private User userWithRole(Long roleId) {
        User user = mock(User.class);
        when(user.getRoleId()).thenReturn(roleId);
        return user;
    }

    private void admins(int count) {
        when(roles.idOfName("ADMIN")).thenReturn(Optional.of(ADMIN_ROLE));
        when(users.findByRoleId(ADMIN_ROLE)).thenReturn(java.util.Collections.nCopies(count, mock(User.class)));
    }

    @Test
    @DisplayName("the only administrator cannot be removed or demoted")
    void lastAdminIsProtected() {
        admins(1);
        assertThrows(BusinessRuleViolationException.class, () -> guard.assertNotLastAdmin(userWithRole(ADMIN_ROLE)));
    }

    @Test
    @DisplayName("an administrator can go while another remains")
    void anotherAdminRemains() {
        admins(2);
        assertDoesNotThrow(() -> guard.assertNotLastAdmin(userWithRole(ADMIN_ROLE)));
    }

    @Test
    @DisplayName("other users are never blocked")
    void nonAdminsAreFree() {
        admins(1);
        assertDoesNotThrow(() -> guard.assertNotLastAdmin(userWithRole(OTHER_ROLE)));
        assertDoesNotThrow(() -> guard.assertNotLastAdmin(userWithRole(null)));
    }
}
