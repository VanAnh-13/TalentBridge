package com.example.talentbridge.user.infrastructure.company;

import com.example.talentbridge.company.application.ReleaseCompanyOwnership;
import com.example.talentbridge.user.application.port.OwnedCompanies;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyModuleOwnedCompanies implements OwnedCompanies {

    private final ReleaseCompanyOwnership releaseCompanyOwnership;

    @Override
    public void releaseOwnedBy(Long userId) {
        releaseCompanyOwnership.execute(userId);
    }
}
