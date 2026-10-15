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
