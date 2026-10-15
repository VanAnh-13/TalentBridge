import { createAsyncThunk } from "@reduxjs/toolkit";
import { toast } from "sonner";

import { auth } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";

import type { Credentials } from "../../domain/account";

export const login = createAsyncThunk(
  "auth/login",
  async (credentials: Credentials, thunkAPI) => {
    try {
      return await auth.login(credentials);
    } catch (err) {
      const message = errorMessage(err, "Đăng nhập thất bại");
      toast.error(message);
      return thunkAPI.rejectWithValue(message);
    }
  },
);

export const logout = createAsyncThunk("auth/logout", async (_, thunkAPI) => {
  try {
    await auth.logout();
  } catch (err) {
    return thunkAPI.rejectWithValue(errorMessage(err, "Đăng xuất thất bại"));
  }
});

export const getAccount = createAsyncThunk("auth/account", async (_, thunkAPI) => {
  try {
    return await auth.currentAccount();
  } catch (err) {
    return thunkAPI.rejectWithValue(errorMessage(err, "Lấy thông tin tài khoản thất bại"));
  }
});
