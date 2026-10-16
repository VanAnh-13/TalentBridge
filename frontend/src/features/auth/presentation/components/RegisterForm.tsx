import { auth } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import type { Registration } from "../../domain/account";
import type { ParseKeys } from "i18next";
import type React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "sonner";

export default function RegisterForm() {
  const { t } = useTranslation(["auth", "common"]);
  const [form, setForm] = useState<Registration>({
    name: "",
    email: "",
    password: "",
    dob: "",
    address: "",
    gender: "OTHER",
    recruiter: false,
  });

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<ParseKeys<"auth"> | "">("");
  const navigate = useNavigate();

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validateEmail = (email: string): boolean => {
    const regex = /^[\w.-]+@([\w-]+\.)+[\w-]{2,4}$/;
    return regex.test(email);
  };

  const validateDateOfBirth = (dob: string): boolean => {
    if (!dob) return true; // Optional field

    const birthDate = new Date(dob);
    const today = new Date();
    const minDate = new Date(
      today.getFullYear() - 120,
      today.getMonth(),
      today.getDate(),
    );

    return birthDate <= today && birthDate >= minDate;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setIsLoading(true);

      if (!form.name || !form.email || !form.password) {
        setError("validation.fillRequired");
        return;
      }

      if (!validateEmail(form.email)) {
        setError("validation.invalidEmail");
        return;
      }

      if (form.dob && !validateDateOfBirth(form.dob)) {
        setError("validation.invalidDob");
        return;
      }

      await auth.register(form);
      toast.info(t("register.success"));
      navigate("/auth?mode=login");
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    } finally {
      setIsLoading(false);
      setError("");
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
                  {t("register.title")}
                </h1>
                <p className="text-gray-600">
                  {t("register.subtitle")}
                </p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Name Field */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("register.name")} <span className="text-orange-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={form.name}
                    onChange={handleChange}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder={t("register.namePlaceholder")}
                  />
                </div>

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
                    placeholder={t("register.passwordPlaceholder")}
                  />
                </div>

                {/* Role Field */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("register.role")} <span className="text-orange-500">*</span>
                  </label>
                  <select
                    name="role"
                    value={form.recruiter ? "recruiter" : "candidate"}
                    onChange={(e) => {
                      setForm((prev) => ({
                        ...prev,
                        recruiter: e.target.value === "recruiter",
                      }));
                    }}
                    className="w-full rounded-lg border border-gray-200 bg-white px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                  >
                    <option value="candidate">
                      {t("register.roles.candidate")}
                    </option>
                    <option value="recruiter">
                      {t("register.roles.recruiter")}
                    </option>
                  </select>
                </div>

                {/* Date of Birth */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("register.dob")}
                  </label>
                  <input
                    type="date"
                    name="dob"
                    value={form.dob ?? ""}
                    onChange={handleChange}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    max={new Date().toISOString().split("T")[0]}
                  />
                </div>

                {/* Address */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("common:fields.address")}
                  </label>
                  <input
                    type="text"
                    name="address"
                    value={form.address}
                    onChange={handleChange}
                    className="w-full rounded-lg border border-gray-200 px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                    placeholder={t("register.addressPlaceholder")}
                  />
                </div>

                {/* Gender */}
                <div>
                  <label className="mb-2 block text-sm font-semibold text-gray-700">
                    {t("register.gender")}
                  </label>
                  <select
                    name="gender"
                    value={form.gender}
                    onChange={handleChange}
                    className="w-full rounded-lg border border-gray-200 bg-white px-4 py-3 transition-all duration-200 outline-none focus:border-transparent focus:ring-2 focus:ring-orange-500"
                  >
                    <option value="MALE">{t("register.genders.MALE")}</option>
                    <option value="FEMALE">{t("register.genders.FEMALE")}</option>
                    <option value="OTHER">{t("register.genders.OTHER")}</option>
                  </select>
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
                      {t("register.submitting")}
                    </div>
                  ) : (
                    t("register.submit")
                  )}
                </button>
              </form>

              {/* Login Link */}
              <div className="mt-6 text-center">
                <p className="text-gray-600">
                  {t("register.hasAccount")}{" "}
                  <Link
                    to="/auth?mode=login"
                    className="font-semibold text-orange-500 transition-colors duration-200 hover:text-orange-600 hover:underline"
                  >
                    {t("register.loginLink")}
                  </Link>
                </p>
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
                    d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
                    clipRule="evenodd"
                  />
                </svg>
              </div>
              <h2 className="mb-4 text-2xl font-bold">{t("register.asideTitle")}</h2>
              <p className="text-lg leading-relaxed text-orange-100">
                {t("register.asideText")}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
