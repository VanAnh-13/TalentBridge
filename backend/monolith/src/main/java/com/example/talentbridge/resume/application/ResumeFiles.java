package com.example.talentbridge.resume.application;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.resume.domain.ResumeDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

/** Storage keys and download links of CV files. */
@Component
@RequiredArgsConstructor
class ResumeFiles {

    private static final Duration LINK_LIFETIME = Duration.ofMinutes(15);

    private final FileStorage storage;

    static String folderOf(String email) {
        return "resume/" + email.replaceAll("[^a-zA-Z0-9]", "_");
    }

    static String fileNameOf(Long id, long version) {
        return "resume-" + id + "-" + version + ".pdf";
    }

    static String keyOf(String email, Long id, long version) {
        return folderOf(email) + "/" + fileNameOf(id, version);
    }

    String linkTo(String fileKey) {
        return storage.generatePresignedUrl(fileKey, LINK_LIFETIME);
    }

    ResumeView view(ResumeDetails details) {
        return new ResumeView(details, linkTo(details.resume().getFileKey()));
    }
}
