import type { Level } from "@/features/job/domain/job";

/** An application: one candidate's CV for one job. */
export interface Resume {
  id: number;
  status: string;
  pdfUrl: string;
  user: { id: number; email: string };
  job: {
    id: number;
    name: string;
    location: string;
    skills: string[];
    level: Level;
    description: string;
  };
  company: { id: number; name: string; logoUrl: string };
  createdAt: string;
  updatedAt: string;
}

/** A recruiter's decision on an application. */
export interface StatusChange {
  id: number;
  status: string;
}

/** What a candidate submits to apply for a job. */
export interface Application {
  userId: number;
  email: string;
  jobId: number;
  pdf: File;
}

/** Which applications a call is about: all of them (administrators) or those to the recruiter's company. */
export type ResumeScope = "all" | "company";
