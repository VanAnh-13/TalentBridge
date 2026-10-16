package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.job.application.port.CompanyDirectory;
import com.example.talentbridge.job.domain.Job;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateJob {

    private final JobRepository jobs;
    private final CompanyDirectory companies;
    private final JobSkills jobSkills;
    private final RecruiterScope recruiterScope;

    /** An administrator edits any job and may move it to another company. */
    public JobDetails execute(Long id, JobData data) {
        Job job = find(id);
        apply(job, data);

        if (data.companyId() != null && !Objects.equals(data.companyId(), job.getCompanyId())) {
            if (!companies.exists(data.companyId()))
                throw new NotFoundException("Company does not exist");
            job.assignTo(data.companyId());
        }

        return save(job, data);
    }

    /** A recruiter edits a job of their own company. */
    public JobDetails executeForCurrentUser(Long id, JobData data) {
        Job job = find(id);
        apply(job, data);

        if (!job.belongsTo(recruiterScope.companyId()))
            throw new ForbiddenException("Access denied");

        return save(job, data);
    }

    private Job find(Long id) {
        return jobs.findById(id).orElseThrow(() -> new NotFoundException("Job not found"));
    }

    private void apply(Job job, JobData d) {
        job.update(d.name(), d.location(), d.salary(), d.quantity(), d.level(), d.description(),
                d.startDate(), d.endDate(), d.active());
    }

    private JobDetails save(Job job, JobData data) {
        if (data.skillIds() != null)
            job.requireSkills(jobSkills.onlyExisting(data.skillIds()));
        return jobs.save(job);
    }
}
