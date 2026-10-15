package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.user.application.port.PasswordHasher;
import com.example.talentbridge.user.application.port.SessionRevoker;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChangeOwnPassword {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final CurrentUser currentUser;
    private final SessionRevoker sessions;

    /**
     * Changes the password and signs the account out everywhere else: a stolen session must not outlive the
     * password. {@code currentRefreshToken} (may be null) keeps the caller's own session.
     */
    public UserDetails execute(String oldPassword, String newPassword, String currentRefreshToken) {
        User user = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        if (!hasher.matches(oldPassword, user.getPasswordHash()))
            throw new BusinessRuleViolationException("Mật khẩu hiện tại không chính xác");

        user.changePasswordHash(hasher.hash(newPassword));
        UserDetails saved = users.save(user);
        sessions.revokeAllExcept(user.getId(), currentRefreshToken);
        return saved;
    }
}
