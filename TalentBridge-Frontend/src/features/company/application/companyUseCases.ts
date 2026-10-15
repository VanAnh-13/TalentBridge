import { allOf, contains } from "@/shared/domain/filter";

import type { CompanyData } from "../domain/company";
import type { CompanyRepository } from "./ports";

export interface CompanyCriteria {
  name?: string;
  address?: string;
}

const filterOf = ({ name = "", address = "" }: CompanyCriteria) =>
  allOf([contains("name", name), contains("address", address)]);

export const createCompanyUseCases = (repository: CompanyRepository) => ({
  search: (criteria: CompanyCriteria, page: number, size: number) =>
    repository.list({ page, size, filter: filterOf(criteria) }),

  /** Same, with the number of open jobs of each company. */
  searchWithJobsCount: (criteria: CompanyCriteria, page: number, size: number) =>
    repository.listWithJobsCount({ page, size, filter: filterOf(criteria) }),

  /** The first companies, for the home page. */
  featured: (size = 5) => repository.list({ page: 0, size, filter: null }),

  find: (id: number) => repository.find(id),

  mine: () => repository.findMine(),

  create: (data: CompanyData, logo: File | null) => repository.create(data, logo),

  createMine: (data: CompanyData, logo: File | null) => repository.createMine(data, logo),

  update: (id: number, data: CompanyData, logo: File | null) => repository.update(id, data, logo),

  updateMine: (data: CompanyData, logo: File | null) => repository.updateMine(data, logo),

  remove: (id: number) => repository.remove(id),

  members: () => repository.members(),

  addMember: (email: string) => repository.addMember(email),

  removeMember: (email: string) => repository.removeMember(email),
});

export type CompanyUseCases = ReturnType<typeof createCompanyUseCases>;
