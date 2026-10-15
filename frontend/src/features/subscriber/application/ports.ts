import type { Subscriber } from "../domain/subscriber";

export interface SubscriberRepository {
  /** The current user's subscription, or null when they have none. */
  findMine(): Promise<Subscriber | null>;
  create(skillIds: number[]): Promise<Subscriber>;
  update(skillIds: number[]): Promise<Subscriber>;
  remove(): Promise<void>;
}
