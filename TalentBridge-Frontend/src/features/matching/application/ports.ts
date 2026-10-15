import type { Match, MatchingStatus, RankedCandidate, Recommendation } from "../domain/match";

export interface MatchingRepository {
  status(): Promise<MatchingStatus>;
  /** The stored assessment, or null while the application has not been scored. */
  ofResume(resumeId: number): Promise<Match | null>;
  /** Queues a new assessment; it shows up in ofResume once the model has answered. */
  recompute(resumeId: number): Promise<void>;
  candidatesOf(jobId: number): Promise<RankedCandidate[]>;
  recommendations(limit: number): Promise<Recommendation[]>;
}
