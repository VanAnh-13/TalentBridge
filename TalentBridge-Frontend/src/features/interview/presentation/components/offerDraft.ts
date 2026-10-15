import type { InterviewMode } from "../../domain/interview";

/** The editable state of an offer: slots are kept as `datetime-local` strings until submitted. */
export interface OfferDraft {
  mode: InterviewMode;
  location: string;
  note: string;
  durationMinutes: number;
  slots: string[];
}

export const emptyDraft = (): OfferDraft => ({
  mode: "ONLINE",
  location: "",
  note: "",
  durationMinutes: 60,
  slots: [""],
});

/** `datetime-local` value (local time, no zone) -> ISO instant; "" when empty/invalid. */
export const toInstant = (local: string) => {
  const time = Date.parse(local);
  return Number.isNaN(time) ? "" : new Date(time).toISOString();
};
