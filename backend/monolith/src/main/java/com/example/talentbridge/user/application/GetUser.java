package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUser {

    private final UserRepository users;
    private final CurrentUser currentUser;

    public UserDetails execute(Long id) {
        return users.findDetailsById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }

    public UserDetails executeForCurrentUser() {
        return users.findDetailsByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }
}
