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
public class CreateCompany {

    private static final String LOGO_FOLDER = "company-logos";

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final FileStorage storage;
    private final CurrentUser currentUser;

    /** An administrator creates a company that has no owner. */
    public Company execute(String name, String description, String address, FileUpload logo) {
        return create(name, description, address, logo, false);
    }

    /** A recruiter creates the company they own and join. */
    public Company executeForCurrentUser(String name, String description, String address, FileUpload logo) {
        return create(name, description, address, logo, true);
    }

    private Company create(String name, String description, String address, FileUpload logo, boolean forCurrentUser) {
        if (companies.existsByNameIgnoreCase(name))
            throw new AlreadyExistsException("Tên công ty đã tồn tại");

        MemberDirectory.Member owner = null;
        if (forCurrentUser) {
            owner = members.findByEmail(currentUser.email())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
            if (owner.companyId() != null)
                throw new AlreadyExistsException("Người dùng đã có công ty");
        }

        Company company = companies.save(Company.create(name, description, address));

        if (owner != null) {
            members.assignToCompany(owner.id(), company.getId());
            company.ownedBy(owner.id());
        }

        if (logo != null && !logo.isEmpty())
            company.changeLogo(storage.uploadFile(logo, LOGO_FOLDER, company.getId().toString(), true));

        return companies.save(company);
    }
}
