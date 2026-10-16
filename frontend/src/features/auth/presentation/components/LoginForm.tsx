import type React from "react";
import type { ParseKeys } from "i18next";

import { errorMessage } from "@/shared/domain/errors";
import type { Credentials } from "../../domain/account";
import { login } from "../state/authThunks";
import { useAuthDispatch } from "../state/hooks";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { toast } from "sonner";

export default function LoginPage() {
  const { t } = useTranslation(["auth", "common"]);
  const [form, setForm] = useState<Credentials>({ email: "", password: "" });
  const [error, setError] = useState<ParseKeys<"auth"> | "">("");
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useAuthDispatch();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateEmail = (email: string): boolean => {
    const regex = /^[\w.-]+@([\w-]+\.)+[\w-]{2,4}$/;
    return regex.test(email);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!form.email || !form.password) {
      setError("validation.fillAll");
      return;
    }

    if (!validateEmail(form.email)) {
      setError("validation.invalidEmail");
      return;
    }

    try {
      setIsLoading(true);
      await dispatch(login(form)).unwrap();
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="w-full max-w-5xl overflow-hidden rounded-2xl bg-white shadow-xl">
        <div className="flex flex-col lg:flex-row">
          {/* Form Section */}
          <div className="flex-1 p-8 lg:p-12">
            <div className="mx-auto max-w-md">
              {/* Header */}
              <div className="mb-8 text-center">
                <h1 className="mb-2 text-3xl font-bold text-gray-900">
                  {t("login.title")}
                </h1>
                <p className="text-gray-600">{t("login.subtitle")}</p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Email Field */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("common:fields.email")} <span className="text-orange-500">*</span>
                  </label>
                  <input
                    type="email"
                    name="email"
                    value={form.email}
                    onChange={handleChange}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder="example@email.com"
                    disabled={isLoading}
                  />
                </div>

                {/* Password Field */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("common:fields.password")} <span className="text-orange-500">*</span>
                  </label>
                  <input
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder={t("login.passwordPlaceholder")}
                    disabled={isLoading}
                  />
                </div>

                {/* Error Message */}
                {error && (
                  <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {t(error)}
                  </div>
                )}

                {/* Submit Button */}
                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full rounded-lg bg-orange-500 px-4 py-3 font-semibold text-white shadow-lg transition-colors duration-200 hover:bg-orange-600 hover:shadow-xl disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isLoading ? (
                    <div className="flex items-center justify-center">
                      <div className="mr-2 h-5 w-5 animate-spin rounded-full border-b-2 border-white"></div>
                      {t("login.submitting")}
                    </div>
                  ) : (
                    t("login.submit")
                  )}
                </button>
              </form>

              {/* Register Link */}
              <div className="mt-6 text-center">
                <p className="text-gray-600">
                  {t("login.noAccount")}{" "}
                  <Link
                    to="/auth?mode=register"
                    className="font-semibold text-orange-500 transition-colors duration-200 hover:text-orange-600 hover:underline"
                  >
                    {t("login.registerLink")}
                  </Link>
                </p>
              </div>

              {/* Divider */}
              <div className="mt-8 mb-6">
                <div className="relative">
                  <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-gray-200"></div>
                  </div>
                  <div className="relative flex justify-center text-sm">
                    <span className="bg-white px-4 text-gray-500">{t("login.or")}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Illustration Section */}
          <div className="flex flex-1 items-center justify-center bg-gradient-to-br from-orange-400 to-orange-500 p-8 lg:p-12">
            <div className="text-center text-white">
              <div className="mx-auto mb-6 flex h-32 w-32 items-center justify-center rounded-full bg-white/20">
                <svg
                  className="h-16 w-16"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path
                    fillRule="evenodd"
                    d="M18 8a6 6 0 01-7.743 5.743L10 14l-1 1-1 1H6v2H2v-4l4.257-4.257A6 6 0 1118 8zm-6-4a1 1 0 100 2 2 2 0 012 2 1 1 0 102 0 4 4 0 00-4-4z"
                    clipRule="evenodd"
                  />
                </svg>
              </div>
              <h2 className="mb-4 text-2xl font-bold">{t("login.asideTitle")}</h2>
              <p className="text-lg leading-relaxed text-orange-100">
                {t("login.asideText")}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
