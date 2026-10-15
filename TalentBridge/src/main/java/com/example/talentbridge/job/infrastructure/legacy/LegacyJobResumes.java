package com.example.talentbridge.job.infrastructure.legacy;

import com.example.talentbridge.job.application.port.JobResumes;
import com.example.talentbridge.model.Resume;
import com.example.talentbridge.repository.ResumeRepository;
import com.example.talentbridge.shared.application.FileStorage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** Temporary adapter over the legacy resume code; replaced when the resume module is migrated. */
@Component
@RequiredArgsConstructor
public class LegacyJobResumes implements JobResumes {

    private final ResumeRepository resumeRepository;
    private final FileStorage storage;
    private final EntityManager entityManager;

    @Override
    public void deleteAllOf(Long jobId) {
        List<Resume> resumes = entityManager
                .createQuery("select r from Resume r where r.job.id = :jobId", Resume.class)
                .setParameter("jobId", jobId)
                .getResultList();

        resumes.forEach(resume -> {
            if (resume.getFileKey() != null)
                storage.deleteFileByKey(resume.getFileKey());
        });
        resumeRepository.deleteAll(resumes);
    }
}
