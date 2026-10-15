import type { Page } from "@/shared/domain/page";

import type { ChatMessage, Conversation } from "../domain/chat";

export interface ChatRepository {
  /** The conversation about an application, created on first use. */
  open(resumeId: number): Promise<Conversation>;
  conversations(page: number, size: number): Promise<Page<Conversation>>;
  /** Newest first, as the server returns them. */
  messages(conversationId: number, page: number, size: number): Promise<Page<ChatMessage>>;
  send(conversationId: number, text: string): Promise<ChatMessage>;
  markRead(conversationId: number): Promise<void>;
  unreadCount(): Promise<number>;
}

export interface ChatListener {
  onMessage(message: ChatMessage): void;
  /** Somebody else has read the conversation up to a message. */
  onRead(receipt: { conversationId: number; readerId: number; upToMessageId: number }): void;
}

export interface ChatStream {
  /** Starts listening; returns the function that stops. */
  subscribe(listener: ChatListener): () => void;
}
