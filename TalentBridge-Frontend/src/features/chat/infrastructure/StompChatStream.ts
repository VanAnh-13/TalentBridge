import type { StompConnection } from "@/shared/infrastructure/realtime/StompConnection";

import type { ChatListener, ChatStream } from "../application/ports";
import type { ChatMessage } from "../domain/chat";

export class StompChatStream implements ChatStream {
  private readonly connection: StompConnection;

  constructor(connection: StompConnection) {
    this.connection = connection;
  }

  subscribe(listener: ChatListener) {
    const stopMessages = this.connection.subscribe("/user/queue/chat", (body) => listener.onMessage(body as ChatMessage));
    const stopReceipts = this.connection.subscribe("/user/queue/chat-read", (body) =>
      listener.onRead(body as Parameters<ChatListener["onRead"]>[0]),
    );
    return () => {
      stopMessages();
      stopReceipts();
    };
  }
}
