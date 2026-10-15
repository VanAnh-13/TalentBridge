import type { TokenStore } from "../application/ports";

const KEY = "access_token";

export class LocalStorageTokenStore implements TokenStore {
  get() {
    return localStorage.getItem(KEY);
  }

  save(accessToken: string) {
    localStorage.setItem(KEY, accessToken);
  }

  clear() {
    localStorage.removeItem(KEY);
  }
}
