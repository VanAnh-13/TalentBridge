import { api, plainApi } from "@/shared/infrastructure/http/api";

import type {
  Account,
  AccountDetails,
  AuthSession,
  Credentials,
  DeviceInfo,
  DeviceSession,
  Registration,
} from "../domain/account";
import type { AuthRepository } from "../application/ports";

export class HttpAuthRepository implements AuthRepository {
  login(credentials: Credentials, device: DeviceInfo) {
    return api.post<AuthSession>("/auth/login", { ...credentials, sessionMetaRequest: device });
  }

  register(registration: Registration) {
    return plainApi.post("/auth/register", registration);
  }

  logout() {
    return plainApi.post("/auth/logout", {});
  }

  refresh(device: DeviceInfo) {
    return plainApi.post<AuthSession>("/auth/refresh-token", device);
  }

  currentAccount() {
    return api.get<Account>("/auth/me");
  }

  accountDetails() {
    return api.get<AccountDetails>("/auth/me/details");
  }

  sessions() {
    return api.get<DeviceSession[]>("/auth/sessions");
  }

  endSession(sessionId: string) {
    return api.delete(`/auth/sessions/${sessionId}`);
  }
}
