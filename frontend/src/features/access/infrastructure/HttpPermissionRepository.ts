import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { PermissionRepository } from "../application/ports";
import type { Permission, PermissionData } from "../domain/permission";

export class HttpPermissionRepository implements PermissionRepository {
  list(query: PageQuery) {
    return api.get<Page<Permission>>(`/permissions?${pageQueryString(query)}`);
  }

  listAll() {
    return api.get<Permission[]>("/permissions/all");
  }

  create(permission: PermissionData) {
    return api.post("/permissions", permission);
  }

  update(id: number, permission: PermissionData) {
    return api.put(`/permissions/${id}`, permission);
  }

  remove(id: number) {
    return api.delete(`/permissions/${id}`);
  }
}
