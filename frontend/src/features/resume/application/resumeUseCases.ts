import { allOf, contains } from "@/shared/domain/filter";

import type { Application, ResumeScope, StatusChange } from "../domain/resume";
import type { ResumeRepository } from "./ports";

export interface ResumeCriteria {
  jobName?: string;
  companyName?: string;
}

export const createResumeUseCases = (repository: ResumeRepository) => ({
  search: (scope: ResumeScope, { jobName = "", companyName = "" }: ResumeCriteria, page: number, size: number) =>
    repository.list(scope, {
      page,
      size,
      filter: allOf([contains("job.name", jobName), contains("job.company.name", companyName)]),
    }),

  mine: (page: number, size: number) => repository.listMine({ page, size, filter: null }),

  apply: (application: Application) => repository.submit(application),

  withdraw: (jobId: number) => repository.withdraw(jobId),

  replaceFile: (resumeId: number, pdf: File) => repository.replaceFile(resumeId, pdf),

  changeStatus: (scope: ResumeScope, change: StatusChange) => repository.changeStatus(scope, change),
});

export type ResumeUseCases = ReturnType<typeof createResumeUseCases>;
