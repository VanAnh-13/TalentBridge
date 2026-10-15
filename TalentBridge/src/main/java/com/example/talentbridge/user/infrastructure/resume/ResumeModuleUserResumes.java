package com.example.talentbridge.user.infrastructure.resume;

import com.example.talentbridge.resume.application.ResumesOf;
import com.example.talentbridge.user.application.port.UserResumes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResumeModuleUserResumes implements UserResumes {

    private final ResumesOf resumesOf;

    @Override
    public void deleteAllOf(Long userId) {
        resumesOf.deleteAllOfUser(userId);
    }
}
