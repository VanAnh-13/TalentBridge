package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.domain.UserAccount;
import com.example.talentbridge.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** The account (user, role, permissions) behind an email: what authentication needs. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserAccount {

    private final UserRepository users;

    public Optional<UserAccount> find(String email) {
        return users.findAccountByEmail(email);
    }

    public UserAccount execute(String email) {
        return find(email).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }
}
