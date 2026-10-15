package com.example.talentbridge.resume.infrastructure.user;

import com.example.talentbridge.resume.application.port.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adapts the user module's API to the resume module's {@link UserDirectory} port. */
@Component
@RequiredArgsConstructor
public class UserModuleUserDirectory implements UserDirectory {

    private final com.example.talentbridge.user.application.UserDirectory users;

    @Override
    public Optional<User> findById(Long id) {
        return users.findById(id).map(UserModuleUserDirectory::toUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.findByEmail(email).map(UserModuleUserDirectory::toUser);
    }

    private static User toUser(com.example.talentbridge.user.application.UserDirectory.UserSummary u) {
        return new User(u.id(), u.email(), u.companyId(), "ADMIN".equals(u.roleName()));
    }
}
