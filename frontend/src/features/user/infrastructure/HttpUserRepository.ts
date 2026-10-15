import { api, pageQueryString } from "@/shared/infrastructure/http/api";
import type { Page, PageQuery } from "@/shared/domain/page";

import type { UserRepository } from "../application/ports";
import type { NewUser, PasswordChange, ProfileChange, User, UserChange } from "../domain/user";

const idOf = (id: number | null) => (id === null ? null : { id });

export class HttpUserRepository implements UserRepository {
  list(query: PageQuery) {
    return api.get<Page<User>>(`/users?${pageQueryString(query)}`);
  }

  find(id: number) {
    return api.get<User>(`/users/${id}`);
  }

  create({ companyId, roleId, ...user }: NewUser) {
    return api.post("/users", { ...user, company: idOf(companyId), role: idOf(roleId) });
  }

  /** The backend reads an id of -1 as "no company / no role". */
  update({ companyId, roleId, ...change }: UserChange) {
    return api.put("/users", {
      ...change,
      company: { id: companyId ?? -1 },
      role: { id: roleId ?? -1 },
    });
  }

  remove(id: number) {
    return api.delete(`/users/${id}`);
  }

  updateProfile(change: ProfileChange) {
    return api.post("/users/me/update-profile", change);
  }

  changePassword(change: PasswordChange) {
    return api.post("/users/me/update-password", change);
  }

  changeAvatar(image: File) {
    const form = new FormData();
    form.append("avatar", image);
    return api.post("/users/me/upload-avatar", form);
  }
}
