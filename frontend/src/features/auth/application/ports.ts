import type {
  Account,
  AccountDetails,
  AuthSession,
  Credentials,
  DeviceInfo,
  DeviceSession,
  Registration,
} from "../domain/account";

/** What the use cases need from the backend. */
export interface AuthRepository {
  login(credentials: Credentials, device: DeviceInfo): Promise<AuthSession>;
  register(registration: Registration): Promise<void>;
  logout(): Promise<void>;
  /** A new session from the refresh cookie. */
  refresh(device: DeviceInfo): Promise<AuthSession>;
  currentAccount(): Promise<Account>;
  accountDetails(): Promise<AccountDetails>;
  sessions(): Promise<DeviceSession[]>;
  endSession(sessionId: string): Promise<void>;
}

/** Where the access token is kept between page loads. */
export interface TokenStore {
  get(): string | null;
  save(accessToken: string): void;
  clear(): void;
}

/** Describes the device the application is running on. */
export interface DeviceInfoProvider {
  current(): DeviceInfo;
}
