import { allOf, contains } from "@/shared/domain/filter";

import type { PermissionData } from "../domain/permission";
import type { RoleData } from "../domain/role";
import type { PermissionRepository, RoleRepository } from "./ports";

export const createRoleUseCases = (repository: RoleRepository) => ({
  /** Roles whose name contains the text. */
  search: (name: string, page: number, size: number) =>
    repository.list({ page, size, filter: contains("name", name) }),

  create: (role: RoleData) => repository.create(role),

  update: (id: number, role: RoleData) => repository.update(id, role),

  remove: (id: number) => repository.remove(id),
});

export const createPermissionUseCases = (repository: PermissionRepository) => ({
  /** Permissions whose name and API path contain the given texts. */
  search: (criteria: { name: string; apiPath: string }, page: number, size: number) =>
    repository.list({
      page,
      size,
      filter: allOf([contains("name", criteria.name), contains("apiPath", criteria.apiPath)]),
    }),

  catalog: () => repository.listAll(),

  create: (permission: PermissionData) => repository.create(permission),

  update: (id: number, permission: PermissionData) => repository.update(id, permission),

  remove: (id: number) => repository.remove(id),
});

export type RoleUseCases = ReturnType<typeof createRoleUseCases>;
export type PermissionUseCases = ReturnType<typeof createPermissionUseCases>;
