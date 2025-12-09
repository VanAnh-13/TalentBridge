import type React from "react";
import { useState, useEffect } from "react";
import { Link, useSearchParams, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { verifyResetTokenApi, resetPasswordApi } from "@/services/authApi";
import { validatePassword, validatePasswordMatch } from "@/utils/passwordValidator";
import axios from "axios";

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get("token") || "";

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isVerifying, setIsVerifying] = useState(true);
  const [tokenError, setTokenError] = useState<string | null>(null);
  const [isSuccess, setIsSuccess] = useState(false);

  const passwordValidation = validatePassword(password);
  const passwordsMatch = validatePasswordMatch(password, confirmPassword);

  useEffect(() => {
    const verifyToken = async () => {
      if (!token) {
        setTokenError("Liên kết không hợp lệ");
        setIsVerifying(false);
        return;
      }

      try {
        const response = await verifyResetTokenApi(token);
        const data = response.data.data;
        if (!data.valid) {
          setTokenError(data.message);
        }
      } catch (err) {
        if (axios.isAxiosError(err)) {
          setTokenError(err.response?.data?.message || "Liên kết không hợp lệ");
        } else {
          setTokenError("Có lỗi xảy ra khi xác thực liên kết");
        }
      } finally {
        setIsVerifying(false);
      }
    };

    verifyToken();
  }, [token]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!passwordValidation.valid) {
      setError(passwordValidation.errors[0]);
      return;
    }

    if (!passwordsMatch) {
      setError("Mật khẩu xác nhận không khớp");
      return;
    }

    try {
      setIsLoading(true);
      await resetPasswordApi({ token, password, confirmPassword });
      setIsSuccess(true);
      toast.success("Đặt lại mật khẩu thành công!");
      
      // Auto redirect after 3 seconds
      setTimeout(() => {
        navigate("/auth?mode=login");
      }, 3000);
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

  // Loading state
  if (isVerifying) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-4 border-orange-500 border-t-transparent"></div>
          <p className="text-gray-600">Đang xác thực liên kết...</p>
        </div>
      </div>
    );
  }

  // Token error state
  if (tokenError) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="w-full max-w-md overflow-hidden rounded-2xl bg-white p-8 shadow-xl">
          <div className="text-center">
            <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-red-100">
              <svg className="h-10 w-10 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <h1 className="mb-4 text-2xl font-bold text-gray-900">Liên kết không hợp lệ</h1>
            <p className="mb-6 text-gray-600">{tokenError}</p>
            <Link
              to="/forgot-password"
              className="inline-block rounded-lg bg-orange-500 px-6 py-3 font-semibold text-white transition-colors hover:bg-orange-600"
            >
              Yêu cầu liên kết mới
            </Link>
          </div>
        </div>
      </div>
    );
  }

  // Success state
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
            <h1 className="mb-4 text-2xl font-bold text-gray-900">Đặt lại mật khẩu thành công!</h1>
            <p className="mb-6 text-gray-600">
              Bạn sẽ được chuyển đến trang đăng nhập trong giây lát...
            </p>
            <Link
              to="/auth?mode=login"
              className="inline-block rounded-lg bg-orange-500 px-6 py-3 font-semibold text-white transition-colors hover:bg-orange-600"
            >
              Đăng nhập ngay
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
                <h1 className="mb-2 text-3xl font-bold text-gray-900">Đặt lại mật khẩu</h1>
                <p className="text-gray-600">Tạo mật khẩu mới cho tài khoản của bạn</p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    Mật khẩu mới <span className="text-orange-500">*</span>
                  </label>
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder="Nhập mật khẩu mới"
                    disabled={isLoading}
                  />
                  
                  {/* Password requirements checklist */}
                  <div className="mt-3 space-y-1">
                    <RequirementCheck met={passwordValidation.checks.minLength} text="Ít nhất 8 ký tự" />
                    <RequirementCheck met={passwordValidation.checks.hasUppercase} text="Ít nhất 1 chữ hoa" />
                    <RequirementCheck met={passwordValidation.checks.hasNumber} text="Ít nhất 1 số" />
                  </div>
                </div>

                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    Xác nhận mật khẩu <span className="text-orange-500">*</span>
                  </label>
                  <input
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder="Nhập lại mật khẩu"
                    disabled={isLoading}
                  />
                  {confirmPassword && !passwordsMatch && (
                    <p className="mt-1 text-sm text-red-500">Mật khẩu xác nhận không khớp</p>
                  )}
                </div>

                {error && (
                  <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                  </div>
                )}

                <button
                  type="submit"
                  disabled={isLoading || !passwordValidation.valid || !passwordsMatch}
                  className="w-full rounded-lg bg-orange-500 px-4 py-3 font-semibold text-white shadow-lg transition-colors duration-200 hover:bg-orange-600 hover:shadow-xl disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isLoading ? (
                    <div className="flex items-center justify-center">
                      <div className="mr-2 h-5 w-5 animate-spin rounded-full border-b-2 border-white"></div>
                      Đang xử lý...
                    </div>
                  ) : (
                    "Đặt lại mật khẩu"
                  )}
                </button>
              </form>
            </div>
          </div>

          {/* Illustration Section */}
          <div className="flex flex-1 items-center justify-center bg-gradient-to-br from-orange-400 to-orange-500 p-8 lg:p-12">
            <div className="text-center text-white">
              <div className="mx-auto mb-6 flex h-32 w-32 items-center justify-center rounded-full bg-white/20">
                <svg className="h-16 w-16" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                </svg>
              </div>
              <h2 className="mb-4 text-2xl font-bold">Bảo mật tài khoản</h2>
              <p className="text-lg leading-relaxed text-orange-100">
                Tạo mật khẩu mạnh để bảo vệ tài khoản của bạn
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function RequirementCheck({ met, text }: { met: boolean; text: string }) {
  return (
    <div className="flex items-center text-sm">
      {met ? (
        <svg className="mr-2 h-4 w-4 text-green-500" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
        </svg>
      ) : (
        <svg className="mr-2 h-4 w-4 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v3.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 10.586V7z" clipRule="evenodd" />
        </svg>
      )}
      <span className={met ? "text-green-600" : "text-gray-500"}>{text}</span>
    </div>
  );
}
