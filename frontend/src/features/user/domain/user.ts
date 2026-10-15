import type { CompanyRef } from "@/features/company/domain/company";
import type { RoleRef } from "@/features/access/domain/role";

export type Gender = "MALE" | "FEMALE" | "OTHER";

export interface User {
  id: number;
  name: string;
  email: string;
  dob: string;
  address: string;
  gender: Gender;
  logoUrl: string;
  company: CompanyRef | null;
  role: RoleRef | null;
  createdAt: string;
  updatedAt: string;
}

/** What an administrator fills in to create a user. */
export interface NewUser {
  name: string;
  email: string;
  password: string;
  dob: string;
  address: string;
  gender: Gender;
  companyId: number | null;
  roleId: number | null;
}

/** What an administrator can change about a user; null company/role detaches them. */
export interface UserChange {
  id: number;
  name: string;
  gender: Gender;
  dob: string;
  address: string;
  companyId: number | null;
  roleId: number | null;
}

/** What people can change about their own profile. */
export interface ProfileChange {
  name: string;
  gender: Gender;
  dob: string;
  address: string;
}

export interface PasswordChange {
  oldPassword: string;
  newPassword: string;
}
