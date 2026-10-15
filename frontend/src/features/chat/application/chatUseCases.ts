import { sendable } from "../domain/chat";
import type { ChatListener, ChatRepository, ChatStream } from "./ports";

const PAGE = 30;

export const createChatUseCases = (repository: ChatRepository, stream: ChatStream) => ({
  open: (resumeId: number) => repository.open(resumeId),

  conversations: (page = 1, size = 50) => repository.conversations(page, size),

  /** The last messages of a thread, oldest first; `before` pages further back. */
  async history(conversationId: number, page = 1) {
    const result = await repository.messages(conversationId, page, PAGE);
    return { messages: [...result.content].reverse(), hasOlder: page < result.totalPages };
  },

  async send(conversationId: number, text: string) {
    if (!sendable(text)) throw new Error("Tin nhắn không hợp lệ");
    return repository.send(conversationId, text.trim());
  },

  markRead: (conversationId: number) => repository.markRead(conversationId),

  unreadCount: () => repository.unreadCount(),

  listen: (listener: ChatListener) => stream.subscribe(listener),
});

export type ChatUseCases = ReturnType<typeof createChatUseCases>;
