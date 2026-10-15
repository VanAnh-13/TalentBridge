import type { SubscriberRepository } from "./ports";

export const createSubscriberUseCases = (repository: SubscriberRepository) => ({
  mine: () => repository.findMine(),

  /** Subscribes, or changes the followed skills when a subscription exists already. */
  save: (skillIds: number[], alreadySubscribed: boolean) =>
    alreadySubscribed ? repository.update(skillIds) : repository.create(skillIds),

  unsubscribe: () => repository.remove(),
});

export type SubscriberUseCases = ReturnType<typeof createSubscriberUseCases>;
