/** Connects the pieces that cannot know about each other: the HTTP client, the auth use cases and the store. */
import { sessionRefreshed } from "@/features/auth/presentation/state/authSlice";
import { logout } from "@/features/auth/presentation/state/authThunks";
import { configureHttpClient } from "@/shared/infrastructure/http/httpClient";

import { auth } from "./container";
import { store } from "./store";

export const wireApplication = () => {
  configureHttpClient({
    getAccessToken: auth.accessToken,
    async refreshAccessToken() {
      const session = await auth.refresh();
      store.dispatch(sessionRefreshed(session));
      return session.accessToken;
    },
    onSessionExpired: () => {
      store.dispatch(logout());
    },
  });
};
