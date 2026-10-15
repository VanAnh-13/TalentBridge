package com.example.talentbridge.company.infrastructure.user;

import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adapts the user module's API to the company module's {@link MemberDirectory} port. */
@Component
@RequiredArgsConstructor
public class UserModuleMemberDirectory implements MemberDirectory {

    private final UserDirectory users;

    @Override
    public Optional<Member> findByEmail(String email) {
        return users.findByEmail(email).map(UserModuleMemberDirectory::toMember);
    }

    @Override
    public List<Member> findByCompanyId(Long companyId) {
        return users.findByCompanyId(companyId).stream().map(UserModuleMemberDirectory::toMember).toList();
    }

    @Override
    public void assignToCompany(Long userId, Long companyId) {
        users.assignToCompany(userId, companyId);
    }

    @Override
    public void removeFromCompany(Long userId) {
        users.removeFromCompany(userId);
    }

    @Override
    public void removeAllFromCompany(Long companyId) {
        users.removeAllFromCompany(companyId);
    }

    private static Member toMember(UserDirectory.UserSummary u) {
        return new Member(u.id(), u.name(), u.email(), u.companyId());
    }
}
