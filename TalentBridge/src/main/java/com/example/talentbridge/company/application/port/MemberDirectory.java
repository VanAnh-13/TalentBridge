package com.example.talentbridge.company.application.port;

import java.util.List;
import java.util.Optional;

/** Output port towards the user module: the people who work for a company. */
public interface MemberDirectory {

    record Member(Long id, String name, String email, Long companyId) {
    }

    Optional<Member> findByEmail(String email);

    List<Member> findByCompanyId(Long companyId);

    void assignToCompany(Long userId, Long companyId);

    void removeFromCompany(Long userId);

    /** Everybody who belongs to the company no longer does. */
    void removeAllFromCompany(Long companyId);
}
