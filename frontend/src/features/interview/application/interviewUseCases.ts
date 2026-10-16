import { offerProblems } from "../domain/interview";
import type { InterviewOffer, OfferProblem } from "../domain/interview";
import type { InterviewRepository } from "./ports";

/** Raised before anything is sent, with the problems found (codes the interface translates). */
export class InvalidOfferError extends Error {
  readonly problems: OfferProblem[];

  constructor(problems: OfferProblem[]) {
    super(problems.join(", "));
    this.name = "InvalidOfferError";
    this.problems = problems;
  }
}

const checked = (offer: InterviewOffer) => {
  const problems = offerProblems(offer);
  if (problems.length > 0) throw new InvalidOfferError(problems);
  return { ...offer, location: offer.location.trim(), note: offer.note.trim() };
};

export const createInterviewUseCases = (repository: InterviewRepository) => ({
  mine: (page = 1, size = 10) => repository.mine(page, size),

  ofMyCompany: (page = 1, size = 10) => repository.ofMyCompany(page, size),

  schedule: (resumeId: number, offer: InterviewOffer) => repository.schedule(resumeId, checked(offer)),

  reschedule: (id: number, offer: InterviewOffer) => repository.reschedule(id, checked(offer)),

  cancel: (id: number) => repository.cancel(id),

  confirm: (id: number, slot: string) => repository.confirm(id, slot),

  decline: (id: number) => repository.decline(id),

  calendar: (id: number) => repository.calendar(id),
});

export type InterviewUseCases = ReturnType<typeof createInterviewUseCases>;
