import type { Page, PageQuery } from "@/shared/domain/page";

import type { Permission, PermissionData } from "../domain/permission";
import type { Role, RoleData } from "../domain/role";

export interface RoleRepository {
  list(query: PageQuery): Promise<Page<Role>>;
  create(role: RoleData): Promise<Role>;
  update(id: number, role: RoleData): Promise<Role>;
  remove(id: number): Promise<void>;
}

export interface PermissionRepository {
  list(query: PageQuery): Promise<Page<Permission>>;
  /** The whole catalog, for choosing which permissions a role holds. */
  listAll(): Promise<Permission[]>;
  create(permission: PermissionData): Promise<void>;
  update(id: number, permission: PermissionData): Promise<void>;
  remove(id: number): Promise<void>;
}
