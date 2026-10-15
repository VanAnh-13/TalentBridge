import { allOf, contains } from "@/shared/domain/filter";

import type { NewUser, PasswordChange, ProfileChange, UserChange } from "../domain/user";
import type { UserRepository } from "./ports";

export const createUserUseCases = (repository: UserRepository) => ({
  search: (criteria: { name?: string; email?: string }, page: number, size: number) =>
    repository.list({
      page,
      size,
      filter: allOf([contains("name", criteria.name ?? ""), contains("email", criteria.email ?? "")]),
    }),

  find: (id: number) => repository.find(id),

  create: (user: NewUser) => repository.create(user),

  update: (change: UserChange) => repository.update(change),

  remove: (id: number) => repository.remove(id),

  updateProfile: (change: ProfileChange) => repository.updateProfile(change),

  changePassword: (change: PasswordChange) => repository.changePassword(change),

  changeAvatar: (image: File) => repository.changeAvatar(image),
});

export type UserUseCases = ReturnType<typeof createUserUseCases>;
