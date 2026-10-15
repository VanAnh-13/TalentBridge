import { useDispatch, useSelector } from "react-redux";
import type { ThunkDispatch, UnknownAction } from "@reduxjs/toolkit";

import { hasAnyPermission } from "../../domain/permissions";
import type { AuthState } from "./authSlice";

/** The part of the store this feature owns; the application's store has more. */
type AuthRoot = { auth: AuthState };

export const useAuthDispatch = () =>
  useDispatch<ThunkDispatch<AuthRoot, unknown, UnknownAction>>();

export const useAuthState = () => useSelector((state: AuthRoot) => state.auth);

/** The signed-in account (an empty one when nobody is signed in). */
export const useAccount = () => useAuthState().user;

export const useIsLoggedIn = () => useAuthState().isLogin;

/** True when signed in and holding at least one of the permissions. */
export const useHasPermission = (required: string | string[]) => {
  const { isLogin, user } = useAuthState();
  return isLogin && hasAnyPermission(user, required);
};
