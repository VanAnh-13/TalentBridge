package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.resume.application.port.UserDirectory;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.resume.domain.ResumeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchResumes {

    private final ResumeRepository resumes;
    private final UserDirectory users;
    private final CurrentUser currentUser;
    private final ResumeFiles files;

    /** Every resume (administrators). */
    public PageResult<ResumeView> execute(FilterExpression filter, PageQuery page) {
        return resumes.search(filter, page).map(files::view);
    }

    /** The resumes sent to the caller's company (recruiters). */
    public PageResult<ResumeView> executeForCurrentUserCompany(FilterExpression filter, PageQuery page) {
        UserDirectory.User user = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (user.companyId() == null)
            throw new NotFoundException("The user's company was not found");

        return resumes.searchByCompany(user.companyId(), filter, page).map(files::view);
    }

    /** The caller's own applications (candidates). */
    public PageResult<ResumeView> executeForCurrentUser(FilterExpression filter, PageQuery page) {
        return resumes.searchByUserEmail(currentUser.email(), filter, page).map(files::view);
    }
}
