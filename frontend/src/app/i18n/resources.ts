/**
 * Every message catalogue of the interface, by language and namespace. A feature brings its own namespace
 * (named after the feature) through its public API; "common" holds what all of them share.
 */
import { accessMessages } from "@/features/access";
import { authMessages } from "@/features/auth";
import { chatMessages } from "@/features/chat";
import { companyMessages } from "@/features/company";
import { interviewMessages } from "@/features/interview";
import { jobMessages } from "@/features/job";
import { matchingMessages } from "@/features/matching";
import { notificationMessages } from "@/features/notification";
import { resumeMessages } from "@/features/resume";
import { skillMessages } from "@/features/skill";
import { subscriberMessages } from "@/features/subscriber";
import { userMessages } from "@/features/user";
import { commonMessages } from "@/shared/presentation/i18n/common";

import { appMessages } from "./app";

export const resources = {
  en: {
    common: commonMessages.en,
    app: appMessages.en,
    access: accessMessages.en,
    auth: authMessages.en,
    chat: chatMessages.en,
    company: companyMessages.en,
    interview: interviewMessages.en,
    job: jobMessages.en,
    matching: matchingMessages.en,
    notification: notificationMessages.en,
    resume: resumeMessages.en,
    skill: skillMessages.en,
    subscriber: subscriberMessages.en,
    user: userMessages.en,
  },
  vi: {
    common: commonMessages.vi,
    app: appMessages.vi,
    access: accessMessages.vi,
    auth: authMessages.vi,
    chat: chatMessages.vi,
    company: companyMessages.vi,
    interview: interviewMessages.vi,
    job: jobMessages.vi,
    matching: matchingMessages.vi,
    notification: notificationMessages.vi,
    resume: resumeMessages.vi,
    skill: skillMessages.vi,
    subscriber: subscriberMessages.vi,
    user: userMessages.vi,
  },
} as const;
