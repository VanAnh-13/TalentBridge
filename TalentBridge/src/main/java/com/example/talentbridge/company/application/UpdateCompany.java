package com.example.talentbridge.company.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateCompany {

    private static final String LOGO_FOLDER = "company-logos";

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final FileStorage storage;
    private final CurrentUser currentUser;

    /** An administrator edits any company. */
    public Company execute(Long id, String name, String description, String address, FileUpload logo) {
        Company company = companies.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
        return apply(company, name, description, address, logo);
    }

    /** A recruiter edits the company they belong to. */
    public Company executeForCurrentUser(String name, String description, String address, FileUpload logo) {
        MemberDirectory.Member member = members.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        if (member.companyId() == null)
            throw new NotFoundException("Không tìm thấy công ty người dùng");

        Company company = companies.findById(member.companyId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty người dùng"));
        return apply(company, name, description, address, logo);
    }

    private Company apply(Company company, String name, String description, String address, FileUpload logo) {
        if (companies.existsByNameIgnoreCaseAndIdNot(name, company.getId()))
            throw new AlreadyExistsException("Tên công ty đã tồn tại");

        company.update(name, description, address);

        if (logo != null && !logo.isEmpty())
            company.changeLogo(storage.uploadFile(logo, LOGO_FOLDER, company.getId().toString(), true));

        return companies.save(company);
    }
}
