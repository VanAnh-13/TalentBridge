import type React from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { forgotPasswordApi } from "@/services/authApi";
import { isValidEmail } from "@/utils/passwordValidator";
import axios from "axios";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!email.trim()) {
      setError("Vui lòng nhập email.");
      return;
    }

    if (!isValidEmail(email)) {
      setError("Email không hợp lệ.");
      return;
    }

    try {
      setIsLoading(true);
      await forgotPasswordApi({ email });
      setIsSuccess(true);
      toast.success("Yêu cầu đã được gửi!");
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message || "Có lỗi xảy ra. Vui lòng thử lại.");
      } else {
        setError("Có lỗi xảy ra. Vui lòng thử lại.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="w-full max-w-md overflow-hidden rounded-2xl bg-white p-8 shadow-xl">
          <div className="text-center">
            <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
              <svg className="h-10 w-10 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h1 className="mb-4 text-2xl font-bold text-gray-900">Kiểm tra email của bạn</h1>
            <p className="mb-6 text-gray-600">
              Nếu email <strong>{email}</strong> tồn tại trong hệ thống, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.
            </p>
            <p className="mb-6 text-sm text-gray-500">
              Liên kết sẽ hết hạn sau 15 phút. Vui lòng kiểm tra cả thư mục spam.
            </p>
            <Link
              to="/auth?mode=login"
              className="inline-block rounded-lg bg-orange-500 px-6 py-3 font-semibold text-white transition-colors hover:bg-orange-600"
            >
              Quay lại đăng nhập
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="w-full max-w-5xl overflow-hidden rounded-2xl bg-white shadow-xl">
        <div className="flex flex-col lg:flex-row">
          {/* Form Section */}
          <div className="flex-1 p-8 lg:p-12">
            <div className="mx-auto max-w-md">
              <div className="mb-8 text-center">
                <h1 className="mb-2 text-3xl font-bold text-gray-900">Quên mật khẩu?</h1>
                <p className="text-gray-600">Nhập email để nhận liên kết đặt lại mật khẩu</p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    Email <span className="text-orange-500">*</span>
                  </label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder="example@email.com"
                    disabled={isLoading}
                  />
                </div>

                {error && (
                  <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                  </div>
                )}

                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full rounded-lg bg-orange-500 px-4 py-3 font-semibold text-white shadow-lg transition-colors duration-200 hover:bg-orange-600 hover:shadow-xl disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isLoading ? (
                    <div className="flex items-center justify-center">
                      <div className="mr-2 h-5 w-5 animate-spin rounded-full border-b-2 border-white"></div>
                      Đang gửi...
                    </div>
                  ) : (
                    "Gửi liên kết đặt lại"
                  )}
                </button>
              </form>

              <div className="mt-6 text-center">
                <Link
                  to="/auth?mode=login"
                  className="font-semibold text-orange-500 transition-colors duration-200 hover:text-orange-600 hover:underline"
                >
                  ← Quay lại đăng nhập
                </Link>
              </div>
            </div>
          </div>

          {/* Illustration Section */}
          <div className="flex flex-1 items-center justify-center bg-gradient-to-br from-orange-400 to-orange-500 p-8 lg:p-12">
            <div className="text-center text-white">
              <div className="mx-auto mb-6 flex h-32 w-32 items-center justify-center rounded-full bg-white/20">
                <svg className="h-16 w-16" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
                  <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
                </svg>
              </div>
              <h2 className="mb-4 text-2xl font-bold">Khôi phục tài khoản</h2>
              <p className="text-lg leading-relaxed text-orange-100">
                Chúng tôi sẽ gửi email hướng dẫn đặt lại mật khẩu cho bạn
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
