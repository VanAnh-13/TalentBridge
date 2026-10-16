export type InterviewMode = "ONLINE" | "ONSITE";
export type InterviewStatus = "PROPOSED" | "CONFIRMED" | "DECLINED" | "CANCELLED";

export interface Interview {
  id: number;
  resumeId: number;
  candidateEmail: string | null;
  jobName: string | null;
  companyName: string | null;
  mode: InterviewMode;
  /** A meeting link (online) or an address (on site). */
  location: string;
  note: string | null;
  durationMinutes: number;
  /** ISO instants offered by the recruiter. */
  proposedSlots: string[];
  confirmedSlot: string | null;
  status: InterviewStatus;
  createdAt: string;
}

/** What a recruiter fills in to invite a candidate or to move an interview. */
export interface InterviewOffer {
  mode: InterviewMode;
  location: string;
  note: string;
  durationMinutes: number;
  /** ISO instants, 1 to MAX_SLOTS of them, all in the future. */
  slots: string[];
}

export const MAX_SLOTS = 5;
export const MIN_DURATION_MINUTES = 15;
export const MAX_DURATION_MINUTES = 480;

/** What can be wrong with an offer; the interface turns each code into a sentence. */
export type OfferProblem =
  | "missingLink"
  | "missingAddress"
  | "noSlot"
  | "tooManySlots"
  | "pastSlot"
  | "duplicateSlot"
  | "badDuration";

/** The candidate still has to answer. */
export const isAwaitingAnswer = (interview: Interview) => interview.status === "PROPOSED";

/** Still going to happen (or waiting for an answer). */
export const isActive = (interview: Interview) =>
  interview.status === "PROPOSED" || interview.status === "CONFIRMED";

/** Problems with an offer; empty when it is fine. */
export const offerProblems = (offer: InterviewOffer, now: Date = new Date()): OfferProblem[] => {
  const problems: OfferProblem[] = [];
  if (!offer.location.trim()) problems.push(offer.mode === "ONLINE" ? "missingLink" : "missingAddress");
  if (offer.slots.length === 0) problems.push("noSlot");
  if (offer.slots.length > MAX_SLOTS) problems.push("tooManySlots");
  if (offer.slots.some((slot) => Number.isNaN(Date.parse(slot)) || new Date(slot) <= now)) problems.push("pastSlot");
  if (new Set(offer.slots).size !== offer.slots.length) problems.push("duplicateSlot");
  if (offer.durationMinutes < MIN_DURATION_MINUTES || offer.durationMinutes > MAX_DURATION_MINUTES)
    problems.push("badDuration");
  return problems;
};
