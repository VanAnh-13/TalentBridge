export interface ChatMessage {
  id: number;
  conversationId: number;
  senderId: number;
  senderName: string | null;
  text: string;
  sentAt: string;
}

export interface Conversation {
  id: number;
  resumeId: number;
  /** Who you are talking to: the company for a candidate, the candidate for a recruiter. */
  title: string | null;
  /** The job the conversation is about. */
  subtitle: string | null;
  lastMessage: ChatMessage | null;
  unread: number;
  lastMessageAt: string;
}

export const MAX_LENGTH = 2000;

/** What can be sent: not blank, not longer than the server accepts. */
export const sendable = (text: string) => text.trim().length > 0 && text.trim().length <= MAX_LENGTH;

/** Keeps a thread ordered oldest to newest without duplicates (a message may arrive live and by reload). */
export const merge = (existing: ChatMessage[], incoming: ChatMessage[]): ChatMessage[] => {
  const byId = new Map<number, ChatMessage>();
  [...existing, ...incoming].forEach((message) => byId.set(message.id, message));
  return [...byId.values()].sort((a, b) => a.id - b.id);
};
