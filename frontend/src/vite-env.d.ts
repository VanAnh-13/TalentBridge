/// <reference types="vite/client" />

/** Build-time settings (Vite exposes VITE_* variables; see .env.example). */
interface ImportMetaEnv {
  /** Base URL of the API gateway, e.g. http://localhost:8080 */
  readonly VITE_API_URL?: string;
  /** Contact link shown in the footer; the icon is hidden when empty. */
  readonly VITE_CONTACT_FACEBOOK_URL?: string;
  /** Team name shown in the footer; a translated default is used when empty. */
  readonly VITE_TEAM_NAME?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

/** package.json version, injected by vite.config.ts. */
declare const __APP_VERSION__: string;
