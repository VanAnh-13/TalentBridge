package com.example.talentbridge.job.infrastructure.legacy;

import com.example.talentbridge.job.application.port.RecruiterCompanies;
import com.example.talentbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Temporary adapter over the legacy user code; replaced when the user module is migrated. */
@Component
@RequiredArgsConstructor
public class LegacyRecruiterCompanies implements RecruiterCompanies {

    private final UserRepository userRepository;

    @Override
    public Optional<UserCompany> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> new UserCompany(u.getId(), u.getCompany() == null ? null : u.getCompany().getId()));
    }
}
