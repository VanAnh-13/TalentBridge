package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.job.application.port.CompanyDirectory;
import com.example.talentbridge.job.domain.Job;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateJob {

    private final JobRepository jobs;
    private final CompanyDirectory companies;
    private final JobSkills jobSkills;
    private final RecruiterScope recruiterScope;

    /** An administrator creates a job for any company. */
    public JobDetails execute(JobData data) {
        Job job = newJob(data);

        if (data.companyId() != null) {
            if (!companies.exists(data.companyId()))
                throw new NotFoundException("Công ty không tồn tại");
            job.assignTo(data.companyId());
        }

        return save(job, data);
    }

    /** A recruiter creates a job for their own company, whatever company the request names. */
    public JobDetails executeForCurrentUser(JobData data) {
        Job job = newJob(data);
        job.assignTo(recruiterScope.companyId());
        return save(job, data);
    }

    private Job newJob(JobData d) {
        return Job.create(d.name(), d.location(), d.salary(), d.quantity(), d.level(), d.description(),
                d.startDate(), d.endDate(), d.active());
    }

    private JobDetails save(Job job, JobData data) {
        if (data.skillIds() != null)
            job.requireSkills(jobSkills.requireAllExisting(data.skillIds()));
        return jobs.save(job);
    }
}
