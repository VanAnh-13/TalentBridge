import { api } from "@/shared/infrastructure/http/api";

import type { MatchingRepository } from "../application/ports";
import type { Match, MatchingStatus, RankedCandidate, Recommendation } from "../domain/match";

export class HttpMatchingRepository implements MatchingRepository {
  status() {
    return api.get<MatchingStatus>("/matching/status");
  }

  async ofResume(resumeId: number) {
    const lookup = await api.get<{ scored: boolean; match: Match | null }>(`/matching/resumes/${resumeId}`);
    return lookup.scored ? lookup.match : null;
  }

  async recompute(resumeId: number) {
    await api.post(`/matching/resumes/${resumeId}/recompute`);
  }

  candidatesOf(jobId: number) {
    return api.get<RankedCandidate[]>(`/matching/jobs/${jobId}/candidates`);
  }

  recommendations(limit: number) {
    return api.get<Recommendation[]>(`/matching/me/recommendations?limit=${limit}`);
  }
}
