package com.example.talentbridge.resume.application;

import com.example.talentbridge.resume.domain.ResumeDetails;

/** A resume as shown to its readers, with a short-lived link to the CV file. */
public record ResumeView(ResumeDetails details, String pdfUrl) {
}
