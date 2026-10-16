import i18n from "i18next";

import { DEFAULT_LANGUAGE, LANGUAGES, LOCALES, type Language } from "./types";

/** The language the interface is shown in right now. */
export const currentLanguage = (): Language => {
  const lng = (i18n.resolvedLanguage ?? i18n.language ?? DEFAULT_LANGUAGE).slice(0, 2);
  return (LANGUAGES as readonly string[]).includes(lng) ? (lng as Language) : DEFAULT_LANGUAGE;
};

/** BCP 47 locale for Intl date / number formatting in the current language. */
export const currentLocale = (): string => LOCALES[currentLanguage()];
