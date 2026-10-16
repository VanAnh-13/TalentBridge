package com.example.talentbridge.company.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListCompanyMembers {

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final CurrentUser currentUser;

    public record CompanyMember(Long id, String name, String email, boolean owner) {
    }

    /** The people working for the caller's company, flagging its owner. */
    public List<CompanyMember> execute() {
        MemberDirectory.Member me = members.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (me.companyId() == null)
            throw new NotFoundException("The user has no company");

        Company company = companies.findById(me.companyId())
                .orElseThrow(() -> new NotFoundException("The user's company was not found"));

        return members.findByCompanyId(company.getId()).stream()
                .map(m -> new CompanyMember(m.id(), m.name(), m.email(), company.isOwnedBy(m.id())))
                .toList();
    }
}
