import type { Page, PageQuery } from "@/shared/domain/page";

import type { NewSkill, Skill, SkillChange } from "../domain/skill";

export interface SkillRepository {
  list(query: PageQuery): Promise<Page<Skill>>;
  create(skill: NewSkill): Promise<Skill>;
  update(skill: SkillChange): Promise<void>;
  remove(id: number): Promise<void>;
}
