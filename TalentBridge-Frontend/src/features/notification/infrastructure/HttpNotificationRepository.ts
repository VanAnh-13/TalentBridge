import { api } from "@/shared/infrastructure/http/api";
import type { Page } from "@/shared/domain/page";

import type { NotificationRepository } from "../application/ports";
import type { Notification } from "../domain/notification";

export class HttpNotificationRepository implements NotificationRepository {
  list(page: number, size: number, unreadOnly: boolean) {
    return api.get<Page<Notification>>(`/notifications?page=${page}&size=${size}&unread=${unreadOnly}`);
  }

  async unreadCount() {
    return (await api.get<{ count: number }>("/notifications/unread-count")).count;
  }

  markRead(id: number) {
    return api.patch(`/notifications/${id}/read`);
  }

  markAllRead() {
    return api.post("/notifications/read-all");
  }
}
