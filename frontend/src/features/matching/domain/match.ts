/** The AI assessment of one application. */
export interface Match {
  resumeId: number;
  jobId: number;
  score: number;
  matchedSkills: string[];
  missingSkills: string[];
  summary: string;
  model: string;
  scoredAt: string;
}

/** An applicant of a job; `match` is null until the CV has been scored. */
export interface RankedCandidate {
  resumeId: number;
  candidateId: number;
  email: string;
  status: string;
  match: Match | null;
}

/** A job that fits the skills a candidate follows (a plain skill overlap, not an AI judgement). */
export interface Recommendation {
  jobId: number;
  name: string;
  companyName: string | null;
  location: string | null;
  skills: string[];
  fitPercent: number;
  sharedSkills: string[];
}

export interface MatchingStatus {
  enabled: boolean;
  model: string | null;
}

/** How well an application fits; presentation shows it as matching:tone.<tone>. */
export type ScoreTone = "excellent" | "good" | "partial" | "poor";

/** 90-100 excellent, 70-89 good, 40-69 partial, below that poor (the bands the AI is told to use). */
export const toneOf = (score: number): ScoreTone =>
  score >= 90 ? "excellent" : score >= 70 ? "good" : score >= 40 ? "partial" : "poor";
