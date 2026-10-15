package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.job.application.port.JobResumes;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteJob {

    private final JobRepository jobs;
    private final JobResumes resumes;
    private final RecruiterScope recruiterScope;

    /** An administrator deletes any job. */
    public JobDetails execute(Long id) {
        return delete(find(id));
    }

    /** A recruiter deletes a job of their own company. */
    public JobDetails executeForCurrentUser(Long id) {
        Long companyId = recruiterScope.companyId();
        JobDetails details = find(id);
        if (!details.job().belongsTo(companyId))
            throw new ForbiddenException("Không có quyền truy cập");
        return delete(details);
    }

    private JobDetails find(Long id) {
        return jobs.findDetailsById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));
    }

    private JobDetails delete(JobDetails details) {
        resumes.deleteAllOf(details.job().getId());
        jobs.delete(details.job());
        return details;
    }
}
