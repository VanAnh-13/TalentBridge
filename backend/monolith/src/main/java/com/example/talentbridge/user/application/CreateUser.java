package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.application.port.PasswordHasher;
import com.example.talentbridge.user.application.port.CompanyCatalog;
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
public class CreateUser {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final CompanyCatalog companies;
    private final RoleCatalog roles;

    /** An administrator creates an account; email and password are trimmed. */
    public UserDetails execute(UserData data) {
        if (users.existsByEmail(data.email()))
            throw new AlreadyExistsException("Email đã tồn tại");

        User user = User.create(data.email().trim(), data.name(), hasher.hash(data.password().trim()),
                data.dob(), data.address(), data.gender());

        if (data.companyId() != null) {
            if (!companies.exists(data.companyId()))
                throw new NotFoundException("Không tìm thấy công ty");
            user.joinCompany(data.companyId());
        }
        if (data.roleId() != null) {
            if (!roles.exists(data.roleId()))
                throw new NotFoundException("Không tìm thấy chức vụ");
            user.assignRole(data.roleId());
        }

        return users.save(user);
    }
}
