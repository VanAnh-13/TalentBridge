package com.example.talentbridge.chat.application;

import com.example.talentbridge.chat.application.port.Accounts;
import com.example.talentbridge.chat.application.port.ChatPublisher;
import com.example.talentbridge.chat.application.port.Notifier;
import com.example.talentbridge.chat.domain.ChatRepository;
import com.example.talentbridge.chat.domain.Conversation;
import com.example.talentbridge.chat.domain.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class SendMessage {

    private static final int PREVIEW = 80;

    private final ChatRepository chat;
    private final ChatAccess access;
    private final ChatPublisher publisher;
    private final Notifier notifier;
    private final Clock clock;

    /**
     * Stores the message and delivers it live to the other people in the conversation. Those who are not connected
     * get one notification for a run of unread messages (not one per message): it is asked for with the first unread
     * one, and dropped by the notification service if the person is online.
     */
    public Message execute(Long conversationId, String text) {
        Accounts.Account me = access.caller();
        Conversation conversation = access.conversationFor(conversationId, me);

        Instant now = Instant.now(clock);
        Message saved = chat.save(Message.write(conversationId, me.id(), text, now));
        conversation.spokeAt(now);
        chat.save(conversation);
        chat.markRead(conversationId, me.id());   // what you write, you have read

        for (Accounts.Account other : access.participants(conversation)) {
            if (other.id().equals(me.id())) continue;
            publisher.messageSent(other.id(), saved, me.name());
            if (chat.unreadIn(conversationId, other.id()) == 1)
                notifier.notifyIfOffline(other.id(), "Tin nhắn mới từ " + me.name(), preview(saved.getText()),
                        "/chat?conversation=" + conversationId);
        }
        return saved;
    }

    private static String preview(String text) {
        return text.length() <= PREVIEW ? text : text.substring(0, PREVIEW - 1) + "…";
    }
}
