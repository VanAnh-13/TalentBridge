import { ApiError } from "@/shared/domain/errors";
import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { CompanyRepository } from "../application/ports";
import type { Company, CompanyData, Member } from "../domain/company";

/** The backend takes the company as a JSON part and the logo as a file part of one multipart request. */
const multipart = (data: CompanyData, logo: File | null) => {
  const form = new FormData();
  form.append("company", new Blob([JSON.stringify(data)], { type: "application/json" }));
  if (logo) form.append("logoFile", logo);
  return form;
};

export class HttpCompanyRepository implements CompanyRepository {
  list(query: PageQuery) {
    return api.get<Page<Company>>(`/companies?${pageQueryString(query)}`);
  }

  listWithJobsCount(query: PageQuery) {
    return api.get<Page<Company>>(`/companies/with-jobs-count?${pageQueryString(query)}`);
  }

  find(id: number) {
    return api.get<Company>(`/companies/${id}`);
  }

  /** A recruiter who has not set up a company yet gets a 404, which is an expected state, not a failure. */
  async findMine() {
    try {
      return (await api.get<Company | null>("/companies/me")) ?? null;
    } catch (err) {
      if (err instanceof ApiError && err.status === 404) return null;
      throw err;
    }
  }

  create(data: CompanyData, logo: File | null) {
    return api.postForm("/companies", multipart(data, logo));
  }

  createMine(data: CompanyData, logo: File | null) {
    return api.postForm("/companies/me", multipart(data, logo));
  }

  update(id: number, data: CompanyData, logo: File | null) {
    return api.putForm(`/companies/${id}`, multipart(data, logo));
  }

  updateMine(data: CompanyData, logo: File | null) {
    return api.putForm("/companies/me", multipart(data, logo));
  }

  remove(id: number) {
    return api.delete(`/companies/${id}`);
  }

  members() {
    return api.get<Member[]>("/companies/me/users");
  }

  addMember(email: string) {
    return api.post("/companies/me/users", { email });
  }

  removeMember(email: string) {
    return api.put("/companies/me/users", { email });
  }
}
