package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.user.domain.Gender;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateOwnProfile {

    private final UserRepository users;
    private final CurrentUser currentUser;

    public UserDetails execute(String name, LocalDate dob, String address, Gender gender) {
        User user = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.updateProfile(name, dob, address, gender);
        return users.save(user);
    }
}
