import type { ReactNode } from "react";

import { useHasPermission } from "../state/hooks";

interface HasPermissionProps {
  perm: string | string[];
  children: ReactNode;
}

/** Renders its children only when the signed-in account holds one of the permissions. */
const HasPermission = ({ perm, children }: HasPermissionProps) => {
  const allowed = useHasPermission(perm);
  return allowed ? <>{children}</> : null;
};

export default HasPermission;
