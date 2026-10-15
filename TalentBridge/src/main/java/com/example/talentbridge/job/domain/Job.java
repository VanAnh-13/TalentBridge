package com.example.talentbridge.job.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** A vacancy. The company and the required skills are referenced by id. */
public class Job {

    private final Long id;
    private String name;
    private String location;
    private Double salary;
    private Integer quantity;
    private Level level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private Boolean active;
    private Long companyId;
    private List<Long> skillIds;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Job(Long id, Long companyId, List<Long> skillIds, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.skillIds = skillIds == null ? null : new ArrayList<>(skillIds);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Job create(String name, String location, Double salary, Integer quantity, Level level,
                             String description, Instant startDate, Instant endDate, Boolean active) {
        Job job = new Job(null, null, null, null, null);
        job.describe(name, location, salary, quantity, level, description, startDate, endDate, active);
        return job;
    }

    public static Job restore(Long id, String name, String location, Double salary, Integer quantity, Level level,
                              String description, Instant startDate, Instant endDate, Boolean active,
                              Long companyId, List<Long> skillIds, Instant createdAt, Instant updatedAt) {
        Job job = new Job(id, companyId, skillIds, createdAt, updatedAt);
        job.describe(name, location, salary, quantity, level, description, startDate, endDate, active);
        return job;
    }

    public void update(String name, String location, Double salary, Integer quantity, Level level,
                       String description, Instant startDate, Instant endDate, Boolean active) {
        describe(name, location, salary, quantity, level, description, startDate, endDate, active);
    }

    private void describe(String name, String location, Double salary, Integer quantity, Level level,
                          String description, Instant startDate, Instant endDate, Boolean active) {
        if (name == null || name.isBlank())
            throw new InvalidRequestException("Tên công việc không được để trống");
        if (level == null)
            throw new InvalidRequestException("Cấp bậc không được để trống");
        this.name = name;
        this.location = location;
        this.salary = salary;
        this.quantity = quantity;
        this.level = level;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public void assignTo(Long companyId) {
        this.companyId = companyId;
    }

    public boolean belongsTo(Long companyId) {
        return this.companyId != null && this.companyId.equals(companyId);
    }

    public void requireSkills(List<Long> ids) {
        this.skillIds = ids == null ? null : new ArrayList<>(ids);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Double getSalary() { return salary; }
    public Integer getQuantity() { return quantity; }
    public Level getLevel() { return level; }
    public String getDescription() { return description; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public Boolean getActive() { return active; }
    public Long getCompanyId() { return companyId; }
    /** {@code null} when the skill set has never been defined. */
    public List<Long> getSkillIds() { return skillIds == null ? null : List.copyOf(skillIds); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
