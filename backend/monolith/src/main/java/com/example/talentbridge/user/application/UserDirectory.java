package com.example.talentbridge.user.application;

import com.example.talentbridge.user.application.port.RoleNames;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** The user module's API for other modules: who someone is, which company they work for, which role they hold. */
@Service
@Transactional
@RequiredArgsConstructor
public class UserDirectory {

    /** What other modules may know about a user. */
    public record UserSummary(Long id, String name, String email, Long companyId, String roleName) {
    }

    private final UserRepository users;
    private final RoleNames roleNames;

    @Transactional(readOnly = true)
    public Optional<UserSummary> findByEmail(String email) {
        return users.findByEmail(email).map(this::summary);
    }

    @Transactional(readOnly = true)
    public Optional<UserSummary> findById(Long id) {
        return users.findById(id).map(this::summary);
    }

    @Transactional(readOnly = true)
    public List<UserSummary> findByCompanyId(Long companyId) {
        return users.findByCompanyId(companyId).stream().map(this::summary).toList();
    }

    public void assignToCompany(Long userId, Long companyId) {
        users.findById(userId).ifPresent(user -> {
            user.joinCompany(companyId);
            users.save(user);
        });
    }

    public void removeFromCompany(Long userId) {
        users.findById(userId).ifPresent(user -> {
            user.leaveCompany();
            users.save(user);
        });
    }

    public void removeAllFromCompany(Long companyId) {
        users.findByCompanyId(companyId).forEach(user -> {
            user.leaveCompany();
            users.save(user);
        });
    }

    /** Users that held the role no longer have any role. */
    public void detachRole(Long roleId) {
        users.findByRoleId(roleId).forEach(user -> {
            user.clearRole();
            users.save(user);
        });
    }

    private UserSummary summary(User u) {
        return new UserSummary(u.getId(), u.getName(), u.getEmail(), u.getCompanyId(),
                u.getRoleId() == null ? null : roleNames.nameOf(u.getRoleId()));
    }
}
