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

/** The candidate still has to answer. */
export const isAwaitingAnswer = (interview: Interview) => interview.status === "PROPOSED";

/** Still going to happen (or waiting for an answer). */
export const isActive = (interview: Interview) =>
  interview.status === "PROPOSED" || interview.status === "CONFIRMED";

/** Problems with an offer, in the words shown to the user; empty when it is fine. */
export const offerProblems = (offer: InterviewOffer, now: Date = new Date()): string[] => {
  const problems: string[] = [];
  if (!offer.location.trim())
    problems.push(offer.mode === "ONLINE" ? "Nhập liên kết phòng họp" : "Nhập địa điểm phỏng vấn");
  if (offer.slots.length === 0) problems.push("Chọn ít nhất một khung giờ");
  if (offer.slots.length > MAX_SLOTS) problems.push(`Tối đa ${MAX_SLOTS} khung giờ`);
  if (offer.slots.some((slot) => Number.isNaN(Date.parse(slot)) || new Date(slot) <= now))
    problems.push("Các khung giờ phải ở trong tương lai");
  if (new Set(offer.slots).size !== offer.slots.length) problems.push("Các khung giờ bị trùng nhau");
  if (offer.durationMinutes < 15 || offer.durationMinutes > 480) problems.push("Thời lượng từ 15 đến 480 phút");
  return problems;
};
