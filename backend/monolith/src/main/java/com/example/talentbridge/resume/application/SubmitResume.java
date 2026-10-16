package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.resume.application.port.JobCatalog;
import com.example.talentbridge.resume.application.port.ApplicationNotifier;
import com.example.talentbridge.resume.application.port.ApplicationScoring;
import com.example.talentbridge.resume.application.port.UserDirectory;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.resume.domain.ResumeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmitResume {

    private final ResumeRepository resumes;
    private final UserDirectory users;
    private final JobCatalog jobs;
    private final FileStorage storage;
    private final CurrentUser currentUser;
    private final ResumeFiles files;
    private final ApplicationNotifier notifier;
    private final ApplicationScoring scoring;

    /**
     * The candidate named in the request must be the caller: nobody applies in somebody else's name.
     * The application always starts as PENDING.
     */
    public ResumeDetails execute(String email, Long userId, Long jobId, FileUpload pdf) {
        UserDirectory.User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.email().equals(email))
            throw new NotFoundException("Access denied");
        if (!user.email().equals(currentUser.email()))
            throw new ForbiddenException("Access denied");

        if (!jobs.exists(jobId))
            throw new NotFoundException("Job not found");
        if (resumes.existsByUserIdAndJobId(user.id(), jobId))
            throw new AlreadyExistsException("The user has already applied for this job");
        if (pdf == null || pdf.isEmpty())
            throw new NotFoundException("PDF file not found");

        ResumeDetails saved = resumes.save(Resume.submit(email, user.id(), jobId));
        Resume resume = saved.resume();

        resume.attachFile(storage.uploadFile(pdf, ResumeFiles.folderOf(resume.getEmail()),
                ResumeFiles.fileNameOf(resume.getId(), resume.getVersion()), false));
        ResumeDetails result = resumes.save(resume);
        notifier.submitted(result);
        scoring.cvChanged(resume.getId());
        return result;
    }
}
