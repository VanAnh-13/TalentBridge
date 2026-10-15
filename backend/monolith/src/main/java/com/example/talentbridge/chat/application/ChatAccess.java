package com.example.talentbridge.chat.application;

import com.example.talentbridge.chat.application.port.Accounts;
import com.example.talentbridge.chat.domain.ChatRepository;
import com.example.talentbridge.chat.domain.Conversation;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** Who is calling and whether they may be in a conversation; shared by the chat use cases. */
@Component
@RequiredArgsConstructor
class ChatAccess {

    private final Accounts accounts;
    private final ChatRepository chat;
    private final CurrentUser currentUser;

    Accounts.Account caller() {
        return accounts.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }

    /** The conversation, provided the caller is its candidate or works for its company. */
    Conversation conversationFor(Long conversationId, Accounts.Account me) {
        Conversation conversation = chat.findConversation(conversationId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc trò chuyện"));
        if (!conversation.hasParticipant(me.id(), me.companyId()))
            throw new ForbiddenException("Không có quyền truy cập");
        return conversation;
    }

    /** Everybody in the conversation: the candidate and the company's members. */
    List<Accounts.Account> participants(Conversation conversation) {
        List<Accounts.Account> people = new java.util.ArrayList<>(accounts.membersOfCompany(conversation.getCompanyId()));
        accounts.findById(conversation.getCandidateId()).ifPresent(candidate -> {
            if (people.stream().noneMatch(p -> p.id().equals(candidate.id()))) people.add(candidate);
        });
        return people;
    }
}
