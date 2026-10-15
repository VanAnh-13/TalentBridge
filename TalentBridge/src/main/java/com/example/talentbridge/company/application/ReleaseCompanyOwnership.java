package com.example.talentbridge.company.application;

import com.example.talentbridge.company.domain.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The company owned by a user (if any) stays but loses its owner, e.g. when the user is deleted. */
@Service
@Transactional
@RequiredArgsConstructor
public class ReleaseCompanyOwnership {

    private final CompanyRepository companies;

    public void execute(Long userId) {
        companies.findByOwnerId(userId).ifPresent(company -> {
            company.ownedBy(null);
            companies.save(company);
        });
    }
}
