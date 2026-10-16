import i18n from "i18next";
import { initReactI18next } from "react-i18next";

import { DEFAULT_LANGUAGE, LANGUAGES, type Language } from "@/shared/presentation/i18n/types";

import { resources } from "./resources";

const STORAGE_KEY = "talentbridge.language";

/** The language chosen earlier in this browser, else English. Storage can be unavailable (private mode). */
const savedLanguage = (): Language => {
  try {
    const saved = window.localStorage.getItem(STORAGE_KEY);
    return (LANGUAGES as readonly string[]).includes(saved ?? "") ? (saved as Language) : DEFAULT_LANGUAGE;
  } catch {
    return DEFAULT_LANGUAGE;
  }
};

void i18n.use(initReactI18next).init({
  resources,
  lng: savedLanguage(),
  fallbackLng: DEFAULT_LANGUAGE,
  supportedLngs: LANGUAGES,
  defaultNS: "common",
  ns: Object.keys(resources.en),
  interpolation: { escapeValue: false }, // React escapes already
});

const remember = (lng: string) => {
  document.documentElement.lang = lng;
  try {
    window.localStorage.setItem(STORAGE_KEY, lng);
  } catch {
    // nothing to do: the choice just will not survive a reload
  }
};
remember(i18n.language);
i18n.on("languageChanged", remember);

export default i18n;
