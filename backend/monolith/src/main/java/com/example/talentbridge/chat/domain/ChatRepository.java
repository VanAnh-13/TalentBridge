package com.example.talentbridge.chat.domain;

import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.Optional;

public interface ChatRepository {

    Conversation save(Conversation conversation);

    Optional<Conversation> findConversation(Long id);

    Optional<Conversation> findConversationByResume(Long resumeId);

    /** Conversations the person takes part in, most recently active first. */
    PageResult<Conversation> findConversationsOf(Long userId, Long companyId, PageQuery page);

    Message save(Message message);

    /** Newest first. */
    PageResult<Message> findMessages(Long conversationId, PageQuery page);

    Optional<Message> findLastMessage(Long conversationId);

    /** Messages written by others since the person last read the conversation. */
    long unreadIn(Long conversationId, Long userId);

    long unreadFor(Long userId, Long companyId);

    /** Moves the person's read marker to the newest message; returns that message's id (null when there is none). */
    Long markRead(Long conversationId, Long userId);
}
