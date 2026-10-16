package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
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
public class GetResumeFileLink {

    private final ResumeRepository resumes;
    private final UserDirectory users;
    private final CurrentUser currentUser;
    private final ResumeFiles files;

    /**
     * A CV may be read by its owner, by recruiters of the company that received it, and by administrators.
     * The id alone must not be enough to fetch somebody's CV.
     */
    public String execute(Long id) {
        ResumeDetails details = resumes.findDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Resume not found"));

        UserDirectory.User reader = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean owner = details.resume().isOwnedBy(reader.id());
        boolean sameCompany = reader.companyId() != null && reader.companyId().equals(details.companyId());
        if (!reader.admin() && !owner && !sameCompany)
            throw new ForbiddenException("Access denied");

        return files.linkTo(details.resume().getFileKey());
    }
}
