import type { Page, PageQuery } from "@/shared/domain/page";

import type { Application, Resume, ResumeScope, StatusChange } from "../domain/resume";

export interface ResumeRepository {
  list(scope: ResumeScope, query: PageQuery): Promise<Page<Resume>>;
  listMine(query: PageQuery): Promise<Page<Resume>>;
  submit(application: Application): Promise<void>;
  withdraw(jobId: number): Promise<void>;
  replaceFile(resumeId: number, pdf: File): Promise<void>;
  changeStatus(scope: ResumeScope, change: StatusChange): Promise<void>;
}
