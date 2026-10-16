package com.example.talentbridge.company.application;

import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RemoveCompanyMember {

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final CurrentUser currentUser;

    /** Only the owner of a company can remove one of its members. */
    public void execute(String emailToRemove) {
        MemberDirectory.Member me = members.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (me.companyId() == null)
            throw new NotFoundException("The user has no company");

        Company company = companies.findById(me.companyId())
                .orElseThrow(() -> new NotFoundException("The user's company was not found"));
        if (!company.isOwnedBy(me.id()))
            throw new ForbiddenException("Access denied");

        MemberDirectory.Member recruiter = members.findByEmail(emailToRemove)
                .orElseThrow(() -> new NotFoundException("The user to remove was not found"));
        if (recruiter.companyId() == null)
            throw new NotFoundException("The user to remove has no company");
        if (!Objects.equals(recruiter.companyId(), company.getId()))
            throw new NotFoundException("This user belongs to another company");

        members.removeFromCompany(recruiter.id());
    }
}
