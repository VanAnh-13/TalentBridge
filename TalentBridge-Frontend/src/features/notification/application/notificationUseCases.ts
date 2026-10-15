import type { Notification } from "../domain/notification";
import type { NotificationRepository, NotificationStream } from "./ports";

export const createNotificationUseCases = (repository: NotificationRepository, stream: NotificationStream) => ({
  recent: (size = 10) => repository.list(1, size, false),

  unreadCount: () => repository.unreadCount(),

  markRead: (id: number) => repository.markRead(id),

  markAllRead: () => repository.markAllRead(),

  /** Calls back for every notification that arrives while the user is online. */
  onArrival: (listener: (notification: Notification) => void) => stream.subscribe(listener),
});

export type NotificationUseCases = ReturnType<typeof createNotificationUseCases>;
