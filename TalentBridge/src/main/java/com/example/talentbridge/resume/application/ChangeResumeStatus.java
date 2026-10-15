package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.resume.application.port.UserDirectory;
import com.example.talentbridge.resume.domain.ResumeStatus;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.resume.domain.ResumeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChangeResumeStatus {

    private final ResumeRepository resumes;
    private final UserDirectory users;
    private final CurrentUser currentUser;

    /** An administrator moves any application forward. */
    public ResumeDetails execute(Long id, ResumeStatus status) {
        ResumeDetails details = resumes.findDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy resume"));
        return change(details, status);
    }

    /** A recruiter moves the applications sent to their own company. */
    public ResumeDetails executeForCurrentUserCompany(Long id, ResumeStatus status) {
        UserDirectory.User user = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        if (user.companyId() == null)
            throw new NotFoundException("Không tìm thấy công ty người dùng");

        ResumeDetails details = resumes.findDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy resume"));
        if (!user.companyId().equals(details.companyId()))
            throw new ForbiddenException("Không có quyền truy cập");

        return change(details, status);
    }

    private ResumeDetails change(ResumeDetails details, ResumeStatus status) {
        Resume resume = details.resume();
        resume.changeStatus(status);
        return resumes.save(resume);
    }
}
