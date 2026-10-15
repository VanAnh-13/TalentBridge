package com.example.talentbridge.job.infrastructure.user;

import com.example.talentbridge.job.application.port.RecruiterCompanies;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adapts the user module's API to the job module's {@link RecruiterCompanies} port. */
@Component
@RequiredArgsConstructor
public class UserModuleRecruiterCompanies implements RecruiterCompanies {

    private final UserDirectory users;

    @Override
    public Optional<UserCompany> findByEmail(String email) {
        return users.findByEmail(email).map(u -> new UserCompany(u.id(), u.companyId()));
    }
}
