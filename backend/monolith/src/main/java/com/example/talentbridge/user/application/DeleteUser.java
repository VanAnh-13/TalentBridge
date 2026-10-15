package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.application.port.OwnedCompanies;
import com.example.talentbridge.user.application.port.SessionRevoker;
import com.example.talentbridge.user.application.port.UserResumes;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUser {

    private final UserRepository users;
    private final OwnedCompanies ownedCompanies;
    private final UserResumes userResumes;
    private final LastAdminGuard lastAdminGuard;
    private final SessionRevoker sessions;

    public UserDetails execute(Long id) {
        UserDetails details = users.findDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        lastAdminGuard.assertNotLastAdmin(details.user());
        ownedCompanies.releaseOwnedBy(id);
        userResumes.deleteAllOf(id);
        users.delete(details.user());
        sessions.revokeAll(id);
        return details;
    }
}
