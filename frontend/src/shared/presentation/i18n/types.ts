/**
 * A translation of a message catalogue: exactly the keys of the English original, every leaf a string.
 * Declaring `const vi: Translation<typeof en>` makes the compiler reject a missing or extra key.
 */
export type Translation<T> = {
  -readonly [K in keyof T]: T[K] extends string ? string : Translation<T[K]>;
};

export const LANGUAGES = ["en", "vi"] as const;
export type Language = (typeof LANGUAGES)[number];
export const DEFAULT_LANGUAGE: Language = "en";

/** Locale used for dates and numbers in each language (day-first dates in both). */
export const LOCALES: Record<Language, string> = { en: "en-GB", vi: "vi-VN" };
