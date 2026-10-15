package com.example.talentbridge.resume.infrastructure.persistence;

import com.example.talentbridge.job.infrastructure.persistence.JobJpaEntity;
import com.example.talentbridge.model.User;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.resume.domain.ResumeRepository;
import com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.FilterSpecifications;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResumeRepositoryAdapter implements ResumeRepository {

    private final ResumeJpaRepository jpa;
    private final EntityManager entityManager;
    private final FilterSpecifications filters;

    @Override
    public ResumeDetails save(Resume resume) {
        ResumeJpaEntity entity = resume.getId() == null
                ? new ResumeJpaEntity()
                : jpa.findById(resume.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy resume"));

        entity.setEmail(resume.getEmail());
        entity.setStatus(resume.getStatus());
        entity.setFileKey(resume.getFileKey());
        entity.setVersion(resume.getVersion());
        entity.setUser(entityManager.getReference(User.class, resume.getUserId()));
        entity.setJob(entityManager.getReference(JobJpaEntity.class, resume.getJobId()));

        return toDetails(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<ResumeDetails> findDetailsById(Long id) {
        return jpa.findById(id).map(ResumeRepositoryAdapter::toDetails);
    }

    @Override
    public Optional<ResumeDetails> findDetailsByUserEmailAndJobId(String email, Long jobId) {
        return jpa.findByUserEmailAndJobId(email, jobId).map(ResumeRepositoryAdapter::toDetails);
    }

    @Override
    public Optional<ResumeDetails> findDetailsByUserEmailAndId(String email, Long id) {
        return jpa.findByUserEmailAndId(email, id).map(ResumeRepositoryAdapter::toDetails);
    }

    @Override
    public boolean existsByUserIdAndJobId(Long userId, Long jobId) {
        return jpa.existsByUserIdAndJobId(userId, jobId);
    }

    @Override
    public PageResult<ResumeDetails> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<ResumeJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                ResumeRepositoryAdapter::toDetails);
    }

    @Override
    public PageResult<ResumeDetails> searchByUserEmail(String email, FilterExpression filter, PageQuery page) {
        Specification<ResumeJpaEntity> own = (root, q, cb) -> cb.equal(root.get("user").get("email"), email);
        return searchWith(own, filter, page);
    }

    @Override
    public PageResult<ResumeDetails> searchByCompany(Long companyId, FilterExpression filter, PageQuery page) {
        Specification<ResumeJpaEntity> ofCompany = (root, q, cb) ->
                cb.equal(root.get("job").get("company").get("id"), companyId);
        return searchWith(ofCompany, filter, page);
    }

    private PageResult<ResumeDetails> searchWith(Specification<ResumeJpaEntity> scope, FilterExpression filter, PageQuery page) {
        Specification<ResumeJpaEntity> combined = scope.and(filters.<ResumeJpaEntity>toSpecification(filter));
        return PagingMapper.toPageResult(
                jpa.findAll(combined, PagingMapper.toPageable(page)), ResumeRepositoryAdapter::toDetails);
    }

    @Override
    public List<Resume> findByJobId(Long jobId) {
        return jpa.findByJobId(jobId).stream().map(ResumeRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<Resume> findByUserId(Long userId) {
        return jpa.findByUserId(userId).stream().map(ResumeRepositoryAdapter::toDomain).toList();
    }

    @Override
    public void delete(Resume resume) {
        ResumeJpaEntity entity = jpa.findById(resume.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy resume"));
        // detach from the owners first so the cascade from User / Job cannot bring the row back
        entity.setUser(null);
        entity.setJob(null);
        jpa.delete(jpa.saveAndFlush(entity));
    }

    static Resume toDomain(ResumeJpaEntity e) {
        return Resume.restore(
                e.getId(), e.getEmail(), e.getStatus(), e.getFileKey(), e.getVersion() == null ? 1L : e.getVersion(),
                e.getUser() == null ? null : e.getUser().getId(),
                e.getJob() == null ? null : e.getJob().getId(),
                e.getCreatedAt(), e.getCreatedBy(), e.getUpdatedAt());
    }

    static ResumeDetails toDetails(ResumeJpaEntity e) {
        ResumeDetails.UserSummary user = e.getUser() == null ? null
                : new ResumeDetails.UserSummary(e.getUser().getId(), e.getUser().getEmail());

        ResumeDetails.JobSummary job = null;
        ResumeDetails.CompanySummary company = null;
        JobJpaEntity j = e.getJob();
        if (j != null) {
            job = new ResumeDetails.JobSummary(
                    j.getId(), j.getName(), j.getLocation(),
                    j.getSkills() == null ? List.of() : j.getSkills().stream().map(SkillJpaEntity::getName).toList(),
                    j.getLevel() == null ? null : j.getLevel().toString(),
                    j.getDescription());
            if (j.getCompany() != null)
                company = new ResumeDetails.CompanySummary(
                        j.getCompany().getId(), j.getCompany().getName(),
                        j.getCompany().getCompanyLogo() == null ? null : j.getCompany().getCompanyLogo().getLogoUrl());
        }
        return new ResumeDetails(toDomain(e), user, job, company);
    }
}
