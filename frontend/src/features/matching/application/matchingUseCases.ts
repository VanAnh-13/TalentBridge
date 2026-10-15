import type { MatchingRepository } from "./ports";

export const createMatchingUseCases = (repository: MatchingRepository) => ({
  status: () => repository.status(),

  ofResume: (resumeId: number) => repository.ofResume(resumeId),

  recompute: (resumeId: number) => repository.recompute(resumeId),

  candidatesOf: (jobId: number) => repository.candidatesOf(jobId),

  recommendations: (limit = 6) => repository.recommendations(limit),
});

export type MatchingUseCases = ReturnType<typeof createMatchingUseCases>;
