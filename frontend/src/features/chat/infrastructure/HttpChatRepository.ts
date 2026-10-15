import { api } from "@/shared/infrastructure/http/api";
import type { Page } from "@/shared/domain/page";

import type { ChatRepository } from "../application/ports";
import type { ChatMessage, Conversation } from "../domain/chat";

export class HttpChatRepository implements ChatRepository {
  open(resumeId: number) {
    return api.post<Conversation>(`/chats/resumes/${resumeId}`);
  }

  conversations(page: number, size: number) {
    return api.get<Page<Conversation>>(`/chats?page=${page}&size=${size}`);
  }

  messages(conversationId: number, page: number, size: number) {
    return api.get<Page<ChatMessage>>(`/chats/${conversationId}/messages?page=${page}&size=${size}`);
  }

  send(conversationId: number, text: string) {
    return api.post<ChatMessage>(`/chats/${conversationId}/messages`, { text });
  }

  markRead(conversationId: number) {
    return api.post(`/chats/${conversationId}/read`);
  }

  async unreadCount() {
    return (await api.get<{ count: number }>("/chats/unread-count")).count;
  }
}
