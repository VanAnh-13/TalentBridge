/** Settings of this build of the web app (see .env.example). Empty values mean "not configured". */
export const appConfig = {
  version: __APP_VERSION__,
  facebookUrl: (import.meta.env.VITE_CONTACT_FACEBOOK_URL ?? "").trim(),
  teamName: (import.meta.env.VITE_TEAM_NAME ?? "").trim(),
} as const;
