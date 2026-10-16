package com.example.talentbridge.chat.application;

import com.example.talentbridge.chat.application.port.Accounts;
import com.example.talentbridge.chat.application.port.Applications;
import com.example.talentbridge.chat.application.port.ChatPublisher;
import com.example.talentbridge.chat.domain.ChatRepository;
import com.example.talentbridge.chat.domain.Conversation;
import com.example.talentbridge.chat.domain.Message;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadChats {

    /** A conversation as a list shows it. */
    public record ConversationView(Conversation conversation, String title, String subtitle, Message lastMessage,
                                   long unread) {
    }

    /** A message with its author's name. */
    public record MessageView(Message message, String senderName) {
    }

    private final ChatRepository chat;
    private final ChatAccess access;
    private final Applications applications;
    private final Accounts accounts;
    private final ChatPublisher publisher;

    @Transactional(readOnly = true)
    public PageResult<ConversationView> conversations(PageQuery page) {
        Accounts.Account me = access.caller();
        return chat.findConversationsOf(me.id(), me.companyId(), page).map(c -> view(c, me));
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        Accounts.Account me = access.caller();
        return chat.unreadFor(me.id(), me.companyId());
    }

    @Transactional(readOnly = true)
    public PageResult<MessageView> messages(Long conversationId, PageQuery page) {
        Accounts.Account me = access.caller();
        access.conversationFor(conversationId, me);
        Map<Long, String> names = new HashMap<>();
        return chat.findMessages(conversationId, page).map(m -> new MessageView(m,
                names.computeIfAbsent(m.getSenderId(), id -> accounts.findById(id).map(Accounts.Account::name).orElse("—"))));
    }

    /** Marks the conversation read and lets the others see it. */
    public void markRead(Long conversationId) {
        Accounts.Account me = access.caller();
        Conversation conversation = access.conversationFor(conversationId, me);
        Long upTo = chat.markRead(conversationId, me.id());
        if (upTo == null) return;
        for (Accounts.Account other : access.participants(conversation))
            if (!other.id().equals(me.id()))
                publisher.messagesRead(other.id(), conversationId, me.id(), upTo);
    }

    private ConversationView view(Conversation c, Accounts.Account me) {
        Applications.Application application = applications.find(c.getResumeId()).orElse(null);
        String job = application == null ? "" : application.jobName();
        boolean asCandidate = c.isCandidate(me.id());
        String title = application == null ? "Conversation"
                : asCandidate ? application.companyName() : application.candidateEmail();
        return new ConversationView(c, title, job, chat.findLastMessage(c.getId()).orElse(null), chat.unreadIn(c.getId(), me.id()));
    }
}
