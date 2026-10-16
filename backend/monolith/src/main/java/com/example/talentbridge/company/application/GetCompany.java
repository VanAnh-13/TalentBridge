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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCompany {

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final CurrentUser currentUser;

    public Company execute(Long id) {
        return companies.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found"));
    }

    /** The company the caller belongs to. */
    public Company executeForCurrentUser() {
        MemberDirectory.Member member = members.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (member.companyId() == null)
            throw new NotFoundException("The user's company was not found");

        return companies.findById(member.companyId())
                .orElseThrow(() -> new NotFoundException("The user's company was not found"));
    }
}
