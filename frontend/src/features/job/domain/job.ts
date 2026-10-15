import type { CompanyRef } from "@/features/company/domain/company";
import type { SkillRef } from "@/features/skill/domain/skill";

export type Level = "INTERN" | "FRESHER" | "MIDDLE" | "SENIOR";

export interface Job {
  id: number;
  name: string;
  location: string;
  salary: number;
  quantity: number;
  level: Level;
  description: string;
  startDate: string;
  endDate: string;
  active: boolean;
  company: CompanyRef;
  skills: SkillRef[];
}

/** What a user fills in to post or change a job. */
export interface JobData {
  name: string;
  location: string;
  salary: number;
  quantity: number;
  level: Level;
  description: string;
  startDate: string;
  endDate: string;
  active: boolean;
  company: { id: number } | null;
  skills: { id: number }[];
}

/**
 * Whose jobs a call is about: every job (administrators) or only those of the signed-in recruiter's
 * company. The backend authorises the two differently.
 */
export type JobScope = "all" | "company";
