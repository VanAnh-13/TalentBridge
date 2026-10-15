import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { ResumeRepository } from "../application/ports";
import type { Application, Resume, ResumeScope, StatusChange } from "../domain/resume";

const base = (scope: ResumeScope) => (scope === "all" ? "/resumes" : "/resumes/company");
const statusPath = (scope: ResumeScope) => (scope === "all" ? "/resumes/status" : "/resumes/company/status");

export class HttpResumeRepository implements ResumeRepository {
  list(scope: ResumeScope, query: PageQuery) {
    return api.get<Page<Resume>>(`${base(scope)}?${pageQueryString(query)}`);
  }

  listMine(query: PageQuery) {
    return api.get<Page<Resume>>(`/resumes/me?${pageQueryString(query)}`);
  }

  /** The backend takes the application as a JSON part and the CV as a file part of one multipart request. */
  submit({ userId, email, jobId, pdf }: Application) {
    const form = new FormData();
    form.append("pdfFile", pdf);
    form.append(
      "resume",
      new Blob([JSON.stringify({ email, status: "PENDING", user: { id: userId }, job: { id: jobId } })], {
        type: "application/json",
      }),
    );
    return api.postForm("/resumes", form);
  }

  withdraw(jobId: number) {
    return api.delete(`/resumes/me/jobs/${jobId}`);
  }

  replaceFile(resumeId: number, pdf: File) {
    const form = new FormData();
    form.append("pdfFile", pdf);
    return api.putForm(`/resumes/me/file/${resumeId}`, form);
  }

  changeStatus(scope: ResumeScope, change: StatusChange) {
    return api.put(statusPath(scope), change);
  }
}
