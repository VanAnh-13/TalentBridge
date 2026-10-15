import { byRecency } from "../domain/permissions";
import type { Credentials, Registration } from "../domain/account";
import type { AuthRepository, DeviceInfoProvider, TokenStore } from "./ports";

export const createAuthUseCases = (
  repository: AuthRepository,
  tokens: TokenStore,
  device: DeviceInfoProvider,
) => ({
  async login(credentials: Credentials) {
    const session = await repository.login(credentials, device.current());
    tokens.save(session.accessToken);
    return session;
  },

  register: (registration: Registration) => repository.register(registration),

  async logout() {
    await repository.logout();
    tokens.clear();
  },

  /** Exchanges the refresh cookie for a new access token. */
  async refresh() {
    const session = await repository.refresh(device.current());
    tokens.save(session.accessToken);
    return session;
  },

  /** Forgets the access token without telling the server (the refresh cookie is already gone). */
  forgetToken: () => tokens.clear(),

  currentAccount: () => repository.currentAccount(),

  accountDetails: () => repository.accountDetails(),

  async sessions() {
    return (await repository.sessions()).sort(byRecency);
  },

  endSession: (sessionId: string) => repository.endSession(sessionId),

  accessToken: () => tokens.get(),
});

export type AuthUseCases = ReturnType<typeof createAuthUseCases>;
