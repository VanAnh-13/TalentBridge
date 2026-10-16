package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.application.port.PasswordHasher;
import com.example.talentbridge.user.application.port.RoleCatalog;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterUser {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final RoleCatalog roles;

    /** Self registration: the account gets the built-in role named {@code roleName} (USER or RECRUITER). */
    public UserDetails execute(UserData data, String roleName) {
        if (users.existsByEmail(data.email()))
            throw new AlreadyExistsException("Email already exists");

        User user = User.create(data.email(), data.name(), hasher.hash(data.password()),
                data.dob(), data.address(), data.gender());

        Long roleId = roles.idOfName(roleName)
                .orElseThrow(() -> new NotFoundException("Role USER not found, registration failed"));
        user.assignRole(roleId);

        return users.save(user);
    }
}
