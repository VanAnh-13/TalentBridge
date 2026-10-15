/**
 * Composition root: the only place that knows which implementation backs each port.
 * Presentation asks for use cases here; it never imports infrastructure.
 */
import { createAuthUseCases } from "@/features/auth/application/authUseCases";
import { BrowserDeviceInfoProvider } from "@/features/auth/infrastructure/BrowserDeviceInfoProvider";
import { HttpAuthRepository } from "@/features/auth/infrastructure/HttpAuthRepository";
import { LocalStorageTokenStore } from "@/features/auth/infrastructure/LocalStorageTokenStore";

export const auth = createAuthUseCases(
  new HttpAuthRepository(),
  new LocalStorageTokenStore(),
  new BrowserDeviceInfoProvider(),
);

import { createSkillUseCases } from "@/features/skill/application/skillUseCases";
import { HttpSkillRepository } from "@/features/skill/infrastructure/HttpSkillRepository";

export const skills = createSkillUseCases(new HttpSkillRepository());

import { createSubscriberUseCases } from "@/features/subscriber/application/subscriberUseCases";
import { HttpSubscriberRepository } from "@/features/subscriber/infrastructure/HttpSubscriberRepository";

export const subscribers = createSubscriberUseCases(new HttpSubscriberRepository());
