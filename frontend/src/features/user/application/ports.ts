import type { Page, PageQuery } from "@/shared/domain/page";

import type { NewUser, PasswordChange, ProfileChange, User, UserChange } from "../domain/user";

export interface UserRepository {
  list(query: PageQuery): Promise<Page<User>>;
  find(id: number): Promise<User>;
  create(user: NewUser): Promise<void>;
  update(change: UserChange): Promise<void>;
  remove(id: number): Promise<void>;

  updateProfile(change: ProfileChange): Promise<void>;
  changePassword(change: PasswordChange): Promise<void>;
  changeAvatar(image: File): Promise<void>;
}
