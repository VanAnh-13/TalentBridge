/**
 * Composition root: the only place that knows which implementation backs each port.
 * Presentation asks for use cases here; it never imports infrastructure.
 */
import { createNotificationUseCases } from "@/features/notification/application/notificationUseCases";
import { HttpNotificationRepository } from "@/features/notification/infrastructure/HttpNotificationRepository";
import { StompNotificationStream } from "@/features/notification/infrastructure/StompNotificationStream";
import { createPermissionUseCases, createRoleUseCases } from "@/features/access/application/accessUseCases";
import { HttpPermissionRepository } from "@/features/access/infrastructure/HttpPermissionRepository";
import { HttpRoleRepository } from "@/features/access/infrastructure/HttpRoleRepository";
import { createAuthUseCases } from "@/features/auth/application/authUseCases";
import { BrowserDeviceInfoProvider } from "@/features/auth/infrastructure/BrowserDeviceInfoProvider";
import { HttpAuthRepository } from "@/features/auth/infrastructure/HttpAuthRepository";
import { LocalStorageTokenStore } from "@/features/auth/infrastructure/LocalStorageTokenStore";
import { createCompanyUseCases } from "@/features/company/application/companyUseCases";
import { HttpCompanyRepository } from "@/features/company/infrastructure/HttpCompanyRepository";
import { createInterviewUseCases } from "@/features/interview/application/interviewUseCases";
import { HttpInterviewRepository } from "@/features/interview/infrastructure/HttpInterviewRepository";
import { createJobUseCases } from "@/features/job/application/jobUseCases";
import { HttpJobRepository } from "@/features/job/infrastructure/HttpJobRepository";
import { createResumeUseCases } from "@/features/resume/application/resumeUseCases";
import { HttpResumeRepository } from "@/features/resume/infrastructure/HttpResumeRepository";
import { createSkillUseCases } from "@/features/skill/application/skillUseCases";
import { HttpSkillRepository } from "@/features/skill/infrastructure/HttpSkillRepository";
import { createSubscriberUseCases } from "@/features/subscriber/application/subscriberUseCases";
import { HttpSubscriberRepository } from "@/features/subscriber/infrastructure/HttpSubscriberRepository";
import { createUserUseCases } from "@/features/user/application/userUseCases";
import { HttpUserRepository } from "@/features/user/infrastructure/HttpUserRepository";

export const auth = createAuthUseCases(
  new HttpAuthRepository(),
  new LocalStorageTokenStore(),
  new BrowserDeviceInfoProvider(),
);
export const notifications = createNotificationUseCases(
  new HttpNotificationRepository(),
  new StompNotificationStream(() => auth.accessToken()),
);
export const skills = createSkillUseCases(new HttpSkillRepository());
export const subscribers = createSubscriberUseCases(new HttpSubscriberRepository());
export const roles = createRoleUseCases(new HttpRoleRepository());
export const permissions = createPermissionUseCases(new HttpPermissionRepository());
export const companies = createCompanyUseCases(new HttpCompanyRepository());
export const interviews = createInterviewUseCases(new HttpInterviewRepository());
export const jobs = createJobUseCases(new HttpJobRepository());
export const resumes = createResumeUseCases(new HttpResumeRepository());
export const users = createUserUseCases(new HttpUserRepository());
