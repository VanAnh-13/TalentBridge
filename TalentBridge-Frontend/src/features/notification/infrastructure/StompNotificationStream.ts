import type { StompConnection } from "@/shared/infrastructure/realtime/StompConnection";

import type { NotificationStream } from "../application/ports";
import type { Notification } from "../domain/notification";

export class StompNotificationStream implements NotificationStream {
  private readonly connection: StompConnection;

  constructor(connection: StompConnection) {
    this.connection = connection;
  }

  subscribe(onNotification: (notification: Notification) => void) {
    return this.connection.subscribe("/user/queue/notifications", (body) => onNotification(body as Notification));
  }
}
