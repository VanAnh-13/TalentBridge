import {
  getUserSession,
  loginApi,
  logoutApi,
  refreshTokenApi,
} from "@/services/authApi";
import type { UserLoginRequestDto } from "@/types/user.d.ts";
import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { toast } from "sonner";

// ===========================================
// ASYNC THUNK
// ===========================================
export const login = createAsyncThunk(
  "auth/login",
  async (data: UserLoginRequestDto, thunkAPI) => {
    try {
      const res = await loginApi(data);
      return res.data.data;
    } catch (err: unknown) {
      const message = getErrorMessage(err, "Đăng nhập thất bại");
      toast.error(message);
      return thunkAPI.rejectWithValue(message);
    }
  },
);

export const logout = createAsyncThunk("auth/logout", async (_, thunkAPI) => {
  try {
    await logoutApi();
  } catch (err: unknown) {
    const message = getErrorMessage(err, "Đăng xuất thất bại");
    return thunkAPI.rejectWithValue(message);
  }
});

export const getAccount = createAsyncThunk(
  "auth/account",
  async (_, thunkAPI) => {
    try {
      const res = await getUserSession();
      return res.data.data;
    } catch (err: unknown) {
      const message = getErrorMessage(err, "Lấy thông tin tài khoản thất bại");
      return thunkAPI.rejectWithValue(message);
    }
  },
);

export const refreshToken = createAsyncThunk(
  "auth/refresh-token",
  async (_, thunkAPI) => {
    try {
      const res = await refreshTokenApi();
      return res.data.data;
    } catch (err: unknown) {
      const message = getErrorMessage(err, "Làm mới phiên đăng nhập thất bại");
      return thunkAPI.rejectWithValue(message);
    }
  },
);

export const getErrorMessage = (err: unknown, message: string): string => {
  let res = message;
  if (axios.isAxiosError(err)) {
    const status = err.response?.status;

    if (status === 413) {
      return "File bạn tải lên vượt quá dung lượng cho phép. Vui lòng chọn file nhỏ hơn hoặc nén lại trước khi gửi.";
    }

    const data = err.response?.data as any;
    if (data?.message && typeof data.message === "string") {
      res = data.message;
    }
  }
  return res;
};
