import type { Page, PageQuery } from "@/shared/domain/page";

import type { Company, CompanyData, Member } from "../domain/company";

export interface CompanyRepository {
  list(query: PageQuery): Promise<Page<Company>>;
  listWithJobsCount(query: PageQuery): Promise<Page<Company>>;
  find(id: number): Promise<Company>;
  /** The signed-in recruiter's company, or null when they have none yet. */
  findMine(): Promise<Company | null>;
  create(data: CompanyData, logo: File | null): Promise<void>;
  createMine(data: CompanyData, logo: File | null): Promise<void>;
  update(id: number, data: CompanyData, logo: File | null): Promise<void>;
  updateMine(data: CompanyData, logo: File | null): Promise<void>;
  remove(id: number): Promise<void>;

  members(): Promise<Member[]>;
  addMember(email: string): Promise<void>;
  removeMember(email: string): Promise<void>;
}
