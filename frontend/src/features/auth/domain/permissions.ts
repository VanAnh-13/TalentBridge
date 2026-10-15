import type { Account } from "./account";

/** True when the account holds at least one of the permissions ("METHOD /path"). */
export const hasAnyPermission = (
  account: Pick<Account, "permissions"> | null | undefined,
  required: string | string[],
): boolean => {
  if (!account?.permissions) return false;
  const wanted = Array.isArray(required) ? required : [required];
  return wanted.some((permission) => account.permissions.includes(permission));
};

/** The current session first, then the most recent. */
export const byRecency = (a: { current: boolean; loginAt: string }, b: { current: boolean; loginAt: string }) => {
  if (a.current && !b.current) return -1;
  if (!a.current && b.current) return 1;
  return new Date(b.loginAt).getTime() - new Date(a.loginAt).getTime();
};
