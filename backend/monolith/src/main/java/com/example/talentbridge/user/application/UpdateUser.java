package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
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
public class UpdateUser {

    private final UserRepository users;
    private final CompanyCatalog companies;
    private final RoleCatalog roles;
    private final LastAdminGuard lastAdminGuard;

    /** An administrator edits an account. A company / role id of -1 clears the link; null leaves it unchanged. */
    public UserDetails execute(Long id, UserData data) {
        User user = users.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        user.updateProfile(data.name(), data.dob(), data.address(), data.gender());

        if (data.companyId() != null) {
            if (data.companyId() == -1) user.leaveCompany();
            else {
                if (!companies.exists(data.companyId()))
                    throw new NotFoundException("Không tìm thấy công ty");
                user.joinCompany(data.companyId());
            }
        }
        if (data.roleId() != null) {
            if (data.roleId() == -1) {
                lastAdminGuard.assertNotLastAdmin(user);
                user.clearRole();
            } else {
                if (!roles.exists(data.roleId()))
                    throw new NotFoundException("Không tìm thấy chức vụ");
                if (!data.roleId().equals(user.getRoleId()))
                    lastAdminGuard.assertNotLastAdmin(user);
                user.assignRole(data.roleId());
            }
        }

        return users.save(user);
    }
}
