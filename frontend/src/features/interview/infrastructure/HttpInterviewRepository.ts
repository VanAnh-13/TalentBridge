import { api } from "@/shared/infrastructure/http/api";
import type { Page } from "@/shared/domain/page";

import type { InterviewRepository } from "../application/ports";
import type { Interview, InterviewOffer } from "../domain/interview";

export class HttpInterviewRepository implements InterviewRepository {
  mine(page: number, size: number) {
    return api.get<Page<Interview>>(`/interviews/me?page=${page}&size=${size}`);
  }

  ofMyCompany(page: number, size: number) {
    return api.get<Page<Interview>>(`/interviews/company?page=${page}&size=${size}`);
  }

  schedule(resumeId: number, offer: InterviewOffer) {
    return api.post<Interview>("/interviews", { resumeId, ...offer });
  }

  reschedule(id: number, offer: InterviewOffer) {
    return api.put<Interview>(`/interviews/${id}/reschedule`, offer);
  }

  cancel(id: number) {
    return api.put<Interview>(`/interviews/${id}/cancel`);
  }

  confirm(id: number, slot: string) {
    return api.put<Interview>(`/interviews/${id}/confirm`, { slot });
  }

  decline(id: number) {
    return api.put<Interview>(`/interviews/${id}/decline`);
  }

  calendar(id: number) {
    return api.getBlob(`/interviews/${id}/calendar.ics`);
  }
}
