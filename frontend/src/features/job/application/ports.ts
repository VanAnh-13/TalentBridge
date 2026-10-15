import type { Page, PageQuery } from "@/shared/domain/page";

import type { Job, JobData, JobScope } from "../domain/job";

export interface JobRepository {
  list(scope: JobScope, query: PageQuery): Promise<Page<Job>>;
  find(id: number): Promise<Job>;
  ofCompany(companyId: number): Promise<Job[]>;
  create(scope: JobScope, job: JobData): Promise<void>;
  update(scope: JobScope, id: number, job: JobData): Promise<void>;
  remove(scope: JobScope, id: number): Promise<void>;
}
