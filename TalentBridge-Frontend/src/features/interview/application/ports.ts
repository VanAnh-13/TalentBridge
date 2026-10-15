import type { Page } from "@/shared/domain/page";

import type { Interview, InterviewOffer } from "../domain/interview";

export interface InterviewRepository {
  mine(page: number, size: number): Promise<Page<Interview>>;
  ofMyCompany(page: number, size: number): Promise<Page<Interview>>;
  schedule(resumeId: number, offer: InterviewOffer): Promise<Interview>;
  reschedule(id: number, offer: InterviewOffer): Promise<Interview>;
  cancel(id: number): Promise<Interview>;
  confirm(id: number, slot: string): Promise<Interview>;
  decline(id: number): Promise<Interview>;
  /** The .ics file of a confirmed interview. */
  calendar(id: number): Promise<Blob>;
}
