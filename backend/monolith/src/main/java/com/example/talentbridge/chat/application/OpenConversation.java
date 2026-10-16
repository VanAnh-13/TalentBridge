package com.example.talentbridge.chat.application;

import com.example.talentbridge.chat.application.port.Accounts;
import com.example.talentbridge.chat.application.port.Applications;
import com.example.talentbridge.chat.domain.ChatRepository;
import com.example.talentbridge.chat.domain.Conversation;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class OpenConversation {

    private final ChatRepository chat;
    private final Applications applications;
    private final ChatAccess access;
    private final Clock clock;

    /**
     * The conversation about an application, created on first use. Only the candidate and people working for the
     * company that received the application may open it.
     */
    public Conversation forApplication(Long resumeId) {
        Accounts.Account me = access.caller();
        Applications.Application application = applications.find(resumeId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        boolean candidate = me.id().equals(application.candidateId());
        boolean company = me.companyId() != null && me.companyId().equals(application.companyId());
        if (!candidate && !company)
            throw new ForbiddenException("Access denied");
        if (application.companyId() == null)
            throw new NotFoundException("This application is not linked to any company");

        return chat.findConversationByResume(resumeId).orElseGet(() ->
                chat.save(Conversation.open(resumeId, application.companyId(), application.candidateId(), Instant.now(clock))));
    }
}
