package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.resume.application.port.ApplicationScoring;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.resume.domain.ResumeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplaceResumeFile {

    private final ResumeRepository resumes;
    private final FileStorage storage;
    private final CurrentUser currentUser;
    private final ApplicationScoring scoring;

    /** The caller uploads a new CV file for one of their applications. */
    public ResumeDetails execute(Long id, FileUpload pdf) {
        ResumeDetails details = resumes.findDetailsByUserEmailAndId(currentUser.email(), id)
                .orElseThrow(() -> new NotFoundException("Resume not found"));

        if (pdf == null || pdf.isEmpty())
            throw new NotFoundException("PDF file not found");

        Resume resume = details.resume();
        String newKey = ResumeFiles.keyOf(resume.getEmail(), resume.getId(), resume.nextVersion());

        storage.deleteFileByKey(resume.getFileKey());
        storage.uploadFile(pdf, newKey, false);
        resume.replaceFile(newKey);

        ResumeDetails saved = resumes.save(resume);
        scoring.cvChanged(saved.resume().getId());
        return saved;
    }
}
