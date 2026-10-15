import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { SkillRepository } from "../application/ports";
import type { NewSkill, Skill, SkillChange } from "../domain/skill";

export class HttpSkillRepository implements SkillRepository {
  list(query: PageQuery) {
    return api.get<Page<Skill>>(`/skills?${pageQueryString(query)}`);
  }

  create(skill: NewSkill) {
    return api.post<Skill>("/skills", skill);
  }

  update(skill: SkillChange) {
    return api.put("/skills", skill);
  }

  remove(id: number) {
    return api.delete(`/skills/${id}`);
  }
}
