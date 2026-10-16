import { Languages } from "lucide-react";
import { useTranslation } from "react-i18next";

import { LANGUAGES } from "../i18n/types";
import { currentLanguage } from "../i18n/locale";

/** EN / VI toggle; the choice is remembered in this browser. */
export const LanguageSwitcher = ({ className = "" }: { className?: string }) => {
  const { t, i18n } = useTranslation();
  const active = currentLanguage();

  return (
    <div
      role="group"
      aria-label={t("language.label")}
      className={`inline-flex items-center gap-1 rounded-md border border-gray-200 bg-white p-0.5 text-xs ${className}`}
    >
      <Languages className="ml-1 h-3.5 w-3.5 text-gray-500" aria-hidden />
      {LANGUAGES.map((lng) => (
        <button
          key={lng}
          type="button"
          lang={lng}
          aria-pressed={active === lng}
          title={t(`language.${lng}`)}
          onClick={() => void i18n.changeLanguage(lng)}
          className={`rounded px-1.5 py-0.5 font-semibold uppercase transition-colors ${
            active === lng ? "bg-gray-900 text-white" : "text-gray-600 hover:bg-gray-100"
          }`}
        >
          {lng}
        </button>
      ))}
    </div>
  );
};
