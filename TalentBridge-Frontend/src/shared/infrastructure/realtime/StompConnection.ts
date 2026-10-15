import { Client } from "@stomp/stompjs";
import type { StompSubscription } from "@stomp/stompjs";

import { API_BASE_URL } from "../http/httpClient";

type Handler = (body: unknown) => void;

interface Registration {
  destination: string;
  handler: Handler;
  subscription?: StompSubscription;
}

/**
 * One WebSocket per browser tab, shared by every feature that listens for live events. It opens when the first
 * listener arrives, authenticates with the current access token on every (re)connect, re-subscribes after a
 * reconnect and closes when the last listener leaves.
 */
export class StompConnection {
  private readonly accessToken: () => string | null;
  private client: Client | null = null;
  private readonly registrations = new Set<Registration>();

  constructor(accessToken: () => string | null) {
    this.accessToken = accessToken;
  }

  /** Listens to a destination such as "/user/queue/chat"; returns the function that stops listening. */
  subscribe(destination: string, handler: Handler): () => void {
    const registration: Registration = { destination, handler };
    this.registrations.add(registration);
    this.ensureConnected();
    if (this.client?.connected) this.attach(registration);

    return () => {
      registration.subscription?.unsubscribe();
      this.registrations.delete(registration);
      if (this.registrations.size === 0) this.disconnect();
    };
  }

  private attach(registration: Registration) {
    registration.subscription = this.client?.subscribe(registration.destination, (frame) => {
      registration.handler(JSON.parse(frame.body));
    });
  }

  private ensureConnected() {
    if (this.client) return;
    const client = new Client({
      brokerURL: API_BASE_URL.replace(/^http/, "ws") + "/ws",
      reconnectDelay: 5000,
      beforeConnect: () => {
        client.connectHeaders = { Authorization: `Bearer ${this.accessToken() ?? ""}` };
      },
      onConnect: () => this.registrations.forEach((registration) => this.attach(registration)),
    });
    this.client = client;
    client.activate();
  }

  private disconnect() {
    const client = this.client;
    this.client = null;
    void client?.deactivate();
  }
}
