package com.example.talentbridge.company.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddCompanyMember {

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final CurrentUser currentUser;

    /** Any member of a company can bring in a user who has no company yet. */
    public void execute(String emailToAdd) {
        MemberDirectory.Member me = members.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (me.companyId() == null)
            throw new NotFoundException("The user has no company");

        Company company = companies.findById(me.companyId())
                .orElseThrow(() -> new NotFoundException("The user's company was not found"));

        MemberDirectory.Member recruiter = members.findByEmail(emailToAdd)
                .orElseThrow(() -> new NotFoundException("The user to add was not found"));
        if (recruiter.companyId() != null)
            throw new NotFoundException("The user to add already belongs to a company");

        members.assignToCompany(recruiter.id(), company.getId());
    }
}
