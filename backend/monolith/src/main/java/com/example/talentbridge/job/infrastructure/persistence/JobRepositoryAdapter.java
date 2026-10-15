package com.example.talentbridge.job.infrastructure.persistence;

import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.job.domain.Job;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobRepositoryAdapter implements JobRepository {

    private final JobJpaRepository jpa;
    private final EntityManager entityManager;
    private final FilterSpecifications filters;

    @Override
    public JobDetails save(Job job) {
        JobJpaEntity entity = job.getId() == null
                ? new JobJpaEntity()
                : jpa.findById(job.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));

        entity.setName(job.getName());
        entity.setLocation(job.getLocation());
        entity.setSalary(job.getSalary());
        entity.setQuantity(job.getQuantity());
        entity.setLevel(job.getLevel());
        entity.setDescription(job.getDescription());
        entity.setStartDate(job.getStartDate());
        entity.setEndDate(job.getEndDate());
        entity.setActive(job.getActive());
        entity.setCompany(job.getCompanyId() == null
                ? null
                : entityManager.getReference(CompanyJpaEntity.class, job.getCompanyId()));

        if (job.getSkillIds() != null) {
            List<SkillJpaEntity> skills = new ArrayList<>();
            for (Long skillId : job.getSkillIds())
                skills.add(entityManager.getReference(SkillJpaEntity.class, skillId));
            entity.setSkills(skills);
        }

        return toDetails(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Job> findById(Long id) {
        return jpa.findById(id).map(JobRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<JobDetails> findDetailsById(Long id) {
        return jpa.findById(id).map(JobRepositoryAdapter::toDetails);
    }

    @Override
    public PageResult<JobDetails> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<JobJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                JobRepositoryAdapter::toDetails);
    }

    @Override
    public PageResult<JobDetails> searchByCompany(Long companyId, FilterExpression filter, PageQuery page) {
        Specification<JobJpaEntity> ofCompany = (root, q, cb) -> cb.equal(root.get("company").get("id"), companyId);
        Specification<JobJpaEntity> combined = ofCompany.and(filters.<JobJpaEntity>toSpecification(filter));
        return PagingMapper.toPageResult(
                jpa.findAll(combined, PagingMapper.toPageable(page)), JobRepositoryAdapter::toDetails);
    }

    @Override
    public List<JobDetails> findByCompany(Long companyId) {
        return jpa.findByCompanyId(companyId).stream().map(JobRepositoryAdapter::toDetails).toList();
    }

    @Override
    public List<JobDetails> findLatestBySkillNames(List<String> skillNames, int limit) {
        if (skillNames == null || skillNames.isEmpty())
            return List.of();
        return jpa.findDistinctBySkills_NameInOrderByCreatedAtDesc(skillNames, org.springframework.data.domain.PageRequest.of(0, limit))
                .stream().map(JobRepositoryAdapter::toDetails).toList();
    }

    @Override
    public long countByCompany(Long companyId) {
        return jpa.countByCompanyId(companyId);
    }

    @Override
    public boolean existsActiveByCompany(Long companyId) {
        return jpa.existsByCompanyIdAndActiveTrue(companyId);
    }

    @Override
    public List<Long> findIdsByCompany(Long companyId) {
        return jpa.findByCompanyId(companyId).stream().map(JobJpaEntity::getId).toList();
    }

    @Override
    public void delete(Job job) {
        JobJpaEntity entity = jpa.findById(job.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));
        if (entity.getSkills() != null)
            entity.getSkills().clear();
        jpa.saveAndFlush(entity);
        jpa.delete(entity);
    }

    static Job toDomain(JobJpaEntity e) {
        return Job.restore(
                e.getId(), e.getName(), e.getLocation(), e.getSalary(), e.getQuantity(), e.getLevel(),
                e.getDescription(), e.getStartDate(), e.getEndDate(), e.getActive(),
                e.getCompany() == null ? null : e.getCompany().getId(),
                e.getSkills() == null ? null : e.getSkills().stream().map(SkillJpaEntity::getId).toList(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    static JobDetails toDetails(JobJpaEntity e) {
        CompanyJpaEntity company = e.getCompany();
        JobDetails.CompanySummary companySummary = null;
        if (company != null) {
            String logoUrl = company.getCompanyLogo() == null ? null : company.getCompanyLogo().getLogoUrl();
            companySummary = new JobDetails.CompanySummary(company.getId(), company.getName(), logoUrl, company.getAddress());
        }

        List<JobDetails.SkillSummary> skills = e.getSkills() == null
                ? List.of()
                : e.getSkills().stream().map(s -> new JobDetails.SkillSummary(s.getId(), s.getName())).toList();

        return new JobDetails(toDomain(e), companySummary, skills);
    }
}
