package com.example.talentbridge.chat.infrastructure.persistence;

import com.example.talentbridge.chat.domain.ChatRepository;
import com.example.talentbridge.chat.domain.Conversation;
import com.example.talentbridge.chat.domain.Message;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatRepositoryAdapter implements ChatRepository {

    /** No company id in the database is negative: used where a person works for no company. */
    private static final long NO_COMPANY = -1L;

    private final ConversationJpaRepository conversations;
    private final MessageJpaRepository messages;
    private final ConversationReadJpaRepository reads;

    @Override
    public Conversation save(Conversation c) {
        ConversationJpaEntity e = c.getId() == null
                ? new ConversationJpaEntity()
                : conversations.findById(c.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc trò chuyện"));
        if (c.getId() == null) {
            e.setResumeId(c.getResumeId());
            e.setCompanyId(c.getCompanyId());
            e.setCandidateId(c.getCandidateId());
            e.setCreatedAt(c.getCreatedAt());
        }
        e.setLastMessageAt(c.getLastMessageAt());
        return toDomain(conversations.saveAndFlush(e));
    }

    @Override
    public Optional<Conversation> findConversation(Long id) {
        return conversations.findById(id).map(ChatRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Conversation> findConversationByResume(Long resumeId) {
        return conversations.findByResumeId(resumeId).map(ChatRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Conversation> findConversationsOf(Long userId, Long companyId, PageQuery page) {
        Sort recent = Sort.by(Sort.Direction.DESC, "lastMessageAt", "id");
        return PagingMapper.toPageResult(
                conversations.findByCandidateIdOrCompanyId(userId, companyId == null ? NO_COMPANY : companyId,
                        PageRequest.of(page.page(), page.size(), recent)),
                ChatRepositoryAdapter::toDomain);
    }

    @Override
    public Message save(Message m) {
        MessageJpaEntity e = new MessageJpaEntity();
        e.setConversationId(m.getConversationId());
        e.setSenderId(m.getSenderId());
        e.setText(m.getText());
        e.setSentAt(m.getSentAt());
        return toDomain(messages.saveAndFlush(e));
    }

    @Override
    public PageResult<Message> findMessages(Long conversationId, PageQuery page) {
        return PagingMapper.toPageResult(
                messages.findByConversationId(conversationId,
                        PageRequest.of(page.page(), page.size(), Sort.by(Sort.Direction.DESC, "id"))),
                ChatRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Message> findLastMessage(Long conversationId) {
        return messages.findFirstByConversationIdOrderByIdDesc(conversationId).map(ChatRepositoryAdapter::toDomain);
    }

    @Override
    public long unreadIn(Long conversationId, Long userId) {
        return messages.unreadIn(conversationId, userId);
    }

    @Override
    public long unreadFor(Long userId, Long companyId) {
        return messages.unreadFor(userId, companyId == null ? NO_COMPANY : companyId);
    }

    @Override
    public Long markRead(Long conversationId, Long userId) {
        Long newest = messages.findFirstByConversationIdOrderByIdDesc(conversationId).map(MessageJpaEntity::getId).orElse(null);
        if (newest == null) return null;
        ConversationReadJpaEntity marker = reads.findById(new ConversationReadJpaEntity.Key(conversationId, userId))
                .orElseGet(() -> {
                    ConversationReadJpaEntity fresh = new ConversationReadJpaEntity();
                    fresh.setConversationId(conversationId);
                    fresh.setUserId(userId);
                    return fresh;
                });
        marker.setLastReadMessageId(newest);
        reads.saveAndFlush(marker);
        return newest;
    }

    private static Conversation toDomain(ConversationJpaEntity e) {
        return Conversation.restore(e.getId(), e.getResumeId(), e.getCompanyId(), e.getCandidateId(), e.getCreatedAt(), e.getLastMessageAt());
    }

    private static Message toDomain(MessageJpaEntity e) {
        return Message.restore(e.getId(), e.getConversationId(), e.getSenderId(), e.getText(), e.getSentAt());
    }
}
