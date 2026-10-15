package com.example.talentbridge.company.infrastructure.legacy;

import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.model.User;
import com.example.talentbridge.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Temporary adapter over the legacy user code; replaced when the user module is migrated. */
@Component
@RequiredArgsConstructor
public class LegacyMemberDirectory implements MemberDirectory {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Override
    public Optional<Member> findByEmail(String email) {
        return userRepository.findByEmail(email).map(LegacyMemberDirectory::toMember);
    }

    @Override
    public List<Member> findByCompanyId(Long companyId) {
        return userRepository.findByCompanyId(companyId).stream().map(LegacyMemberDirectory::toMember).toList();
    }

    @Override
    public void assignToCompany(Long userId, Long companyId) {
        User user = userRepository.getReferenceById(userId);
        user.setCompany(entityManager.getReference(CompanyJpaEntity.class, companyId));
        userRepository.saveAndFlush(user);
    }

    @Override
    public void removeFromCompany(Long userId) {
        User user = userRepository.getReferenceById(userId);
        user.setCompany(null);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void removeAllFromCompany(Long companyId) {
        // managed entities, so the persistence context stays consistent (a bulk UPDATE would leave stale state)
        userRepository.findByCompanyId(companyId).forEach(u -> u.setCompany(null));
        userRepository.flush();
    }

    private static Member toMember(User u) {
        return new Member(u.getId(), u.getName(), u.getEmail(), u.getCompany() == null ? null : u.getCompany().getId());
    }
}
