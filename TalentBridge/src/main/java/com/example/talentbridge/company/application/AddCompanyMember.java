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
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        if (me.companyId() == null)
            throw new NotFoundException("Người dùng không có công ty");

        Company company = companies.findById(me.companyId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty người dùng"));

        MemberDirectory.Member recruiter = members.findByEmail(emailToAdd)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng cần thêm"));
        if (recruiter.companyId() != null)
            throw new NotFoundException("Người dùng cần thêm đã có công ty");

        members.assignToCompany(recruiter.id(), company.getId());
    }
}
