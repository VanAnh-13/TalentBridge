import { api } from "@/shared/infrastructure/http/api";

import type { SubscriberRepository } from "../application/ports";
import type { Subscriber } from "../domain/subscriber";

const body = (skillIds: number[]) => ({ skills: skillIds.map((id) => ({ id })) });

export class HttpSubscriberRepository implements SubscriberRepository {
  async findMine() {
    return (await api.get<Subscriber | null>("/subscribers/me")) ?? null;
  }

  create(skillIds: number[]) {
    return api.post<Subscriber>("/subscribers/me", body(skillIds));
  }

  update(skillIds: number[]) {
    return api.put<Subscriber>("/subscribers/me", body(skillIds));
  }

  remove() {
    return api.delete("/subscribers/me");
  }
}
