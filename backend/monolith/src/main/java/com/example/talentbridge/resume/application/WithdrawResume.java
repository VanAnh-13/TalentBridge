package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.resume.domain.ResumeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WithdrawResume {

    private final ResumeRepository resumes;
    private final FileStorage storage;
    private final CurrentUser currentUser;

    /** The caller withdraws their application to a job; its file is deleted too. */
    public ResumeDetails execute(Long jobId) {
        ResumeDetails details = resumes.findDetailsByUserEmailAndJobId(currentUser.email(), jobId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy resume"));

        storage.deleteFileByKey(details.resume().getFileKey());
        resumes.delete(details.resume());
        return details;
    }
}
