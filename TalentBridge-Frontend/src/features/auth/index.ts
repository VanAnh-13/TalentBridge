/** Public API of the auth feature: what other features and the app shell may use. */
export { default as HasPermission } from "./presentation/components/HasPermission";
export { ProtectedRoute } from "./presentation/components/ProtectedRoute";
export { default as UserMenu } from "./presentation/components/UserMenu";
export {
  useAccount,
  useAuthDispatch,
  useAuthState,
  useHasPermission,
  useIsLoggedIn,
} from "./presentation/state/hooks";
export { getAccount } from "./presentation/state/authThunks";
export type { Account, AccountDetails } from "./domain/account";
export { default as AuthPage } from "./presentation/pages/AuthPage";
export { default as SessionsPage } from "./presentation/pages/SessionsPage";
