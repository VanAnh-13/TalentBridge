package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.application.port.PasswordHasher;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sets a new password for an account whose owner proved control of it (password reset). */
@Service
@Transactional
@RequiredArgsConstructor
public class ResetUserPassword {

    private final UserRepository users;
    private final PasswordHasher hasher;

    public void execute(Long userId, String newPassword) {
        User user = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.changePasswordHash(hasher.hash(newPassword));
        users.save(user);
    }
}
