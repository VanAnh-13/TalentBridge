import type { Page } from "@/shared/domain/page";

import type { Notification } from "../domain/notification";

export interface NotificationRepository {
  list(page: number, size: number, unreadOnly: boolean): Promise<Page<Notification>>;
  unreadCount(): Promise<number>;
  markRead(id: number): Promise<void>;
  markAllRead(): Promise<void>;
}

/** Live notifications for the signed-in user. */
export interface NotificationStream {
  /** Starts listening; returns the function that stops. */
  subscribe(onNotification: (notification: Notification) => void): () => void;
}
