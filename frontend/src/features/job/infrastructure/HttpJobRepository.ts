import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { JobRepository } from "../application/ports";
import type { Job, JobData, JobScope } from "../domain/job";

const base = (scope: JobScope) => (scope === "all" ? "/jobs" : "/jobs/company");

export class HttpJobRepository implements JobRepository {
  list(scope: JobScope, query: PageQuery) {
    return api.get<Page<Job>>(`${base(scope)}?${pageQueryString(query)}`);
  }

  find(id: number) {
    return api.get<Job>(`/jobs/${id}`);
  }

  ofCompany(companyId: number) {
    return api.get<Job[]>(`/jobs/companies/${companyId}`);
  }

  create(scope: JobScope, job: JobData) {
    return api.post(base(scope), job);
  }

  update(scope: JobScope, id: number, job: JobData) {
    return api.put(`${base(scope)}/${id}`, job);
  }

  remove(scope: JobScope, id: number) {
    return api.delete(`${base(scope)}/${id}`);
  }
}
