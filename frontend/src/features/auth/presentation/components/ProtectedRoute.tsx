import type { ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { Navigate } from "react-router-dom";
import { toast } from "sonner";

import { hasAnyPermission } from "../../domain/permissions";
import { useAccount } from "../state/hooks";

interface ProtectedRouteProps {
  requiredPermission: string | string[];
  children: ReactNode;
  to: string;
}

export function ProtectedRoute({ children, requiredPermission, to }: ProtectedRouteProps) {
  const { t } = useTranslation("auth");
  const user = useAccount();

  if (!hasAnyPermission(user, requiredPermission)) {
    toast.error(t("errors.accessDenied"));
    return <Navigate to={to} replace />;
  }

  return <>{children}</>;
}
