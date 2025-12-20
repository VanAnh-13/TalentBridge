package com.example.talentbridge.repository;

import com.example.talentbridge.model.Resume;
import com.example.talentbridge.model.constant.ResumeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ResumeRepository extends
                JpaRepository<Resume, Long>,
                JpaSpecificationExecutor<Resume> {

        boolean existsByUserIdAndJobId(Long userId, Long jobId);

        default Page<Resume> findByUserEmail(
                        String email,
                        Specification<Resume> filterSpec,
                        Pageable pageable) {
                Specification<Resume> userSpec = (root, q, cb) -> cb.equal(root.get("user").get("email"), email);

                Specification<Resume> combined = userSpec.and(filterSpec);

                return findAll(combined, pageable);
        }

        default Page<Resume> findByUserCompanyId(
                        Long id,
                        Specification<Resume> filterSpec,
                        Pageable pageable) {
                Specification<Resume> userSpec = (root, q, cb) -> cb.equal(root.get("job").get("company").get("id"),
                                id);

                Specification<Resume> combined = userSpec.and(filterSpec);

                return findAll(combined, pageable);
        }

        Optional<Resume> findByUserEmailAndJobId(String email, Long jobId);

        Optional<Resume> findByUserEmailAndId(String email, Long id);

        long countByCreatedAtBetween(Instant start, Instant end);

        long countByStatus(ResumeStatus status);

}
