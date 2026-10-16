package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.job.application.port.RecruiterCompanies;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Resolves the company the calling recruiter works for. */
@Component
@RequiredArgsConstructor
class RecruiterScope {

    private final RecruiterCompanies recruiters;
    private final CurrentUser currentUser;

    Long companyId() {
        RecruiterCompanies.UserCompany user = recruiters.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (user.companyId() == null)
            throw new NotFoundException("The user's company was not found");
        return user.companyId();
    }
}
