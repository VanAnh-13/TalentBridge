import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

import { ANONYMOUS } from "../../domain/account";
import type { Account, AuthSession } from "../../domain/account";
import { getAccount, login, logout } from "./authThunks";

export type AuthState = {
  user: Account;
  isLogin: boolean;
  isLoading: boolean;
  error: string | null;
};

const initialState: AuthState = {
  user: ANONYMOUS,
  isLogin: false,
  isLoading: false,
  error: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    /** A session was started outside the thunks (the HTTP client refreshed an expired token). */
    sessionRefreshed(state, action: PayloadAction<AuthSession>) {
      state.user = action.payload.user;
      state.isLogin = true;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.user = action.payload.user;
        state.isLogin = true;
        state.isLoading = false;
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })

      .addCase(logout.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(logout.fulfilled, (state) => {
        state.user = ANONYMOUS;
        state.isLogin = false;
        state.isLoading = false;
      })
      .addCase(logout.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })

      .addCase(getAccount.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(getAccount.fulfilled, (state, action) => {
        state.user = action.payload;
        state.isLogin = true;
        state.isLoading = false;
      })
      .addCase(getAccount.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export const { sessionRefreshed } = authSlice.actions;
export default authSlice.reducer;
