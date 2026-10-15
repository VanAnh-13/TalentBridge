package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.resume.domain.Resume;
import com.example.talentbridge.resume.domain.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** What other modules (job, user) may ask of the resume module when their data goes away. */
@Service
@Transactional
@RequiredArgsConstructor
public class ResumesOf {

    private final ResumeRepository resumes;
    private final FileStorage storage;

    /** Deletes the resumes sent to a job together with their files. */
    public void deleteAllOfJob(Long jobId) {
        deleteAll(resumes.findByJobId(jobId));
    }

    /** Deletes the resumes of a candidate together with their files. */
    public void deleteAllOfUser(Long userId) {
        deleteAll(resumes.findByUserId(userId));
    }

    private void deleteAll(List<Resume> list) {
        list.forEach(resume -> {
            if (resume.getFileKey() != null)
                storage.deleteFileByKey(resume.getFileKey());
            resumes.delete(resume);
        });
    }
}
