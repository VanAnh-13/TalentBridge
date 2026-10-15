export interface Skill {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
}

/** A skill as other screens refer to it (a job requires it, a subscriber follows it). */
export interface SkillRef {
  id: number;
  name: string;
}

export interface NewSkill {
  name: string;
}

export interface SkillChange {
  id: number;
  name: string;
}
