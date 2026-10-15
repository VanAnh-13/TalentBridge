/**
 * Composition root: the only place that knows which implementation backs each port.
 * Presentation asks for use cases here; it never imports infrastructure.
 */
import { createAuthUseCases } from "@/features/auth/application/authUseCases";
import { BrowserDeviceInfoProvider } from "@/features/auth/infrastructure/BrowserDeviceInfoProvider";
import { HttpAuthRepository } from "@/features/auth/infrastructure/HttpAuthRepository";
import { LocalStorageTokenStore } from "@/features/auth/infrastructure/LocalStorageTokenStore";

export const auth = createAuthUseCases(
  new HttpAuthRepository(),
  new LocalStorageTokenStore(),
  new BrowserDeviceInfoProvider(),
);

import { createSkillUseCases } from "@/features/skill/application/skillUseCases";
import { HttpSkillRepository } from "@/features/skill/infrastructure/HttpSkillRepository";

export const skills = createSkillUseCases(new HttpSkillRepository());

import { createSubscriberUseCases } from "@/features/subscriber/application/subscriberUseCases";
import { HttpSubscriberRepository } from "@/features/subscriber/infrastructure/HttpSubscriberRepository";

export const subscribers = createSubscriberUseCases(new HttpSubscriberRepository());

import { createPermissionUseCases, createRoleUseCases } from "@/features/access/application/accessUseCases";
import { HttpPermissionRepository } from "@/features/access/infrastructure/HttpPermissionRepository";
import { HttpRoleRepository } from "@/features/access/infrastructure/HttpRoleRepository";

export const roles = createRoleUseCases(new HttpRoleRepository());
export const permissions = createPermissionUseCases(new HttpPermissionRepository());

import { createCompanyUseCases } from "@/features/company/application/companyUseCases";
import { HttpCompanyRepository } from "@/features/company/infrastructure/HttpCompanyRepository";

export const companies = createCompanyUseCases(new HttpCompanyRepository());

import { createJobUseCases } from "@/features/job/application/jobUseCases";
import { HttpJobRepository } from "@/features/job/infrastructure/HttpJobRepository";

export const jobs = createJobUseCases(new HttpJobRepository());

import { createResumeUseCases } from "@/features/resume/application/resumeUseCases";
import { HttpResumeRepository } from "@/features/resume/infrastructure/HttpResumeRepository";

export const resumes = createResumeUseCases(new HttpResumeRepository());
