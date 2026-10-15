import type { SkillRef } from "@/features/skill/domain/skill";

/** A user's subscription to job alerts for a set of skills. */
export interface Subscriber {
  id: number;
  email: string;
  skills: SkillRef[];
}
