import { Client } from "@stomp/stompjs";

import { API_BASE_URL } from "@/shared/infrastructure/http/httpClient";

import type { NotificationStream } from "../application/ports";
import type { Notification } from "../domain/notification";

/** STOMP over WebSocket; authenticates with the current access token on every (re)connect. */
export class StompNotificationStream implements NotificationStream {
  private readonly accessToken: () => string | null;

  constructor(accessToken: () => string | null) {
    this.accessToken = accessToken;
  }

  subscribe(onNotification: (notification: Notification) => void) {
    const client = new Client({
      brokerURL: API_BASE_URL.replace(/^http/, "ws") + "/ws",
      reconnectDelay: 5000,
      beforeConnect: () => {
        client.connectHeaders = { Authorization: `Bearer ${this.accessToken() ?? ""}` };
      },
      onConnect: () => {
        client.subscribe("/user/queue/notifications", (frame) => {
          onNotification(JSON.parse(frame.body) as Notification);
        });
      },
    });
    client.activate();
    return () => {
      void client.deactivate();
    };
  }
}
