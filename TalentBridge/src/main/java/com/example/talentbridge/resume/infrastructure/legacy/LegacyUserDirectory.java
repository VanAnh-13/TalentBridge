package com.example.talentbridge.resume.infrastructure.legacy;

import com.example.talentbridge.resume.application.port.UserDirectory;
import com.example.talentbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Temporary adapter over the legacy user code; replaced when the user module is migrated. */
@Component
@RequiredArgsConstructor
public class LegacyUserDirectory implements UserDirectory {

    private final UserRepository userRepository;

    @Override
    public Optional<UserDirectory.User> findById(Long id) {
        return userRepository.findById(id).map(LegacyUserDirectory::toUser);
    }

    @Override
    public Optional<UserDirectory.User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(LegacyUserDirectory::toUser);
    }

    private static UserDirectory.User toUser(com.example.talentbridge.model.User u) {
        return new UserDirectory.User(
                u.getId(), u.getEmail(),
                u.getCompany() == null ? null : u.getCompany().getId(),
                u.getRole() != null && "ADMIN".equals(u.getRole().getName()));
    }
}
