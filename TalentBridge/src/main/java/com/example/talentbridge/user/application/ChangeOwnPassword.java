package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.user.application.port.PasswordHasher;
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

    public UserDetails execute(String oldPassword, String newPassword) {
        User user = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        if (!hasher.matches(oldPassword, user.getPasswordHash()))
            throw new BusinessRuleViolationException("Mật khẩu hiện tại không chính xác");

        user.changePasswordHash(hasher.hash(newPassword));
        return users.save(user);
    }
}
