import { allOf, contains } from "@/shared/domain/filter";

import type { JobData, JobScope } from "../domain/job";
import type { JobRepository } from "./ports";

export interface JobCriteria {
  name?: string;
  companyName?: string;
  /** A level name, or "all" / empty for any. */
  level?: string;
  location?: string;
}

const filterOf = ({ name = "", companyName = "", level = "", location = "" }: JobCriteria) =>
  allOf([
    contains("name", name),
    contains("company.name", companyName),
    level && level !== "all" ? `level : '${level}'` : null,
    contains("location", location),
  ]);

export const createJobUseCases = (repository: JobRepository) => ({
  search: (scope: JobScope, criteria: JobCriteria, page: number, size: number) =>
    repository.list(scope, { page, size, filter: filterOf(criteria) }),

  find: (id: number) => repository.find(id),

  ofCompany: (companyId: number) => repository.ofCompany(companyId),

  create: (scope: JobScope, job: JobData) => repository.create(scope, job),

  update: (scope: JobScope, id: number, job: JobData) => repository.update(scope, id, job),

  remove: (scope: JobScope, id: number) => repository.remove(scope, id),
});

export type JobUseCases = ReturnType<typeof createJobUseCases>;
