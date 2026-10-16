import { Edit, Trash2, Wrench } from "lucide-react";
import { Button } from "@/shared/presentation/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { formatISO } from "@/shared/presentation/format.ts";
import type { Role } from "../../domain/role";
import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { HasPermission } from "@/features/auth";
import { useTranslation } from "react-i18next";

interface RoleTableProps {
  roles: Role[];
  isLoading: boolean;
  onEdit: (role: Role) => void;
  onDelete: (id: number) => void;
}

export function RoleTable({
  roles,
  isLoading,
  onEdit,
  onDelete,
}: RoleTableProps) {
  const { t } = useTranslation(["access", "common"]);
  return (
    <div className="overflow-hidden rounded-lg border border-blue-600">
      <Table>
        <TableHeader className="bg-blue-600 text-white">
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("role.fields.name")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.status")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.createdAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.updatedAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.actions")}
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell colSpan={6}>
                <div className="flex justify-center py-6">
                  <LoadingSpinner />
                </div>
              </TableCell>
            </TableRow>
          ) : roles.length === 0 ? (
            <TableRow>
              <TableCell colSpan={6}>
                <EmptyState
                  title={t("role.list.emptyTitle")}
                  description={t("role.list.emptyDescription")}
                  icon={
                    <Wrench className="text-muted-foreground mb-4 h-12 w-12" />
                  }
                />
              </TableCell>
            </TableRow>
          ) : (
            roles.map((role) => (
              <TableRow key={role.id}>
                <TableCell className="text-center">{role.id}</TableCell>
                <TableCell className="text-center">{role.name}</TableCell>
                <TableCell className="text-center">
                  {role.active ? (
                    <Badge className={"bg-green-500 text-white"}>
                      {t("common:states.active")}
                    </Badge>
                  ) : (
                    <Badge className={"bg-red-500 text-white"}>
                      {t("common:states.inactive")}
                    </Badge>
                  )}
                </TableCell>
                <TableCell className="text-center">
                  {formatISO(role.createdAt)}
                </TableCell>
                <TableCell className="text-center">
                  {formatISO(role.updatedAt)}
                </TableCell>
                <TableCell>
                  <div className="flex items-center justify-center gap-2">
                    <HasPermission perm={"PUT /roles/{id}"}>
                      <Button
                        size="icon"
                        variant="ghost"
                        className="text-orange-500 hover:text-orange-600"
                        onClick={() => onEdit(role)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                    </HasPermission>

                    <HasPermission perm={"DELETE /roles/{id}"}>
                      <DeleteConfirmDialog onConfirm={() => onDelete(role.id)}>
                        <Button
                          size="icon"
                          variant="ghost"
                          className="text-red-500 hover:text-red-600"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </DeleteConfirmDialog>
                    </HasPermission>
                  </div>
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  );
}
