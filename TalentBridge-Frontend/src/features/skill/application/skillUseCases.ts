import type { PageQuery } from "@/shared/domain/page";

import type { NewSkill, SkillChange } from "../domain/skill";
import type { SkillRepository } from "./ports";

/** The server-side filter for "name contains text"; null when there is nothing to search for. */
export const skillNameFilter = (text: string): string | null => (text ? `name ~ '*${text}*'` : null);

export const createSkillUseCases = (repository: SkillRepository) => ({
  list: (query: PageQuery) => repository.list(query),

  /** Skills whose name contains the text, one page at a time. */
  search: (text: string, page: number, size: number) =>
    repository.list({ page, size, filter: skillNameFilter(text) }),

  create: (skill: NewSkill) => repository.create(skill),

  update: (skill: SkillChange) => repository.update(skill),

  remove: (id: number) => repository.remove(id),
});

export type SkillUseCases = ReturnType<typeof createSkillUseCases>;
