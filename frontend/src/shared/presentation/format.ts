import i18n from "i18next";
import { convert } from "html-to-text";

import { currentLocale } from "./i18n/locale";

export function formatISO(isoString: string): string {
  const date = new Date(isoString);
  return date.toLocaleString(currentLocale(), {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

export function formatISOToYMD(isoString: string): string {
  const date = new Date(isoString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function getFirstLineFromHtml(html: string): string {
  const plainText = convert(html, {
    wordwrap: false,
    selectors: [{ selector: "a", format: "inline" }],
  });

  return (
    plainText
      .split(/\r?\n/)
      .find((line) => line.trim() !== "")
      ?.trim() ?? ""
  );
}

export const formatSalary = (salary: number) => {
  if (salary === 0) return i18n.t("salary.negotiable");
  return `${salary.toLocaleString(currentLocale())} VND`;
};

/** How long ago something happened, in the interface language ("5 minutes ago"). */
export const formatLoginTime = (dateStr: string) => {
  const date = new Date(dateStr);
  const diffMs = Date.now() - date.getTime();

  const diffMinutes = Math.floor(diffMs / (1000 * 60));
  const diffHours = Math.floor(diffMinutes / 60);
  const diffDays = Math.floor(diffHours / 24);
  const relative = new Intl.RelativeTimeFormat(currentLocale(), { numeric: "auto" });

  if (diffMinutes < 1) return i18n.t("time.justNow");
  if (diffMinutes < 60) return relative.format(-diffMinutes, "minute");
  if (diffHours < 24) return relative.format(-diffHours, "hour");
  if (diffDays < 7) return relative.format(-diffDays, "day");
  return date.toLocaleDateString(currentLocale(), {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });
};
