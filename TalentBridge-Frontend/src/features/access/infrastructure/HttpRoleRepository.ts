import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { RoleRepository } from "../application/ports";
import type { Role, RoleData } from "../domain/role";

export class HttpRoleRepository implements RoleRepository {
  list(query: PageQuery) {
    return api.get<Page<Role>>(`/roles?${pageQueryString(query)}`);
  }

  create(role: RoleData) {
    return api.post<Role>("/roles", role);
  }

  update(id: number, role: RoleData) {
    return api.put<Role>(`/roles/${id}`, role);
  }

  remove(id: number) {
    return api.delete(`/roles/${id}`);
  }
}
