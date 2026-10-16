import { Edit, KeyRound, Trash2 } from "lucide-react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table.tsx";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog.tsx";
import { EmptyState } from "@/shared/presentation/components/EmptyState.tsx";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import { formatISO } from "@/shared/presentation/format.ts";

import type { User } from "../../domain/user";
import { HasPermission } from "@/features/auth";
import { useTranslation } from "react-i18next";

interface UserTableProps {
  users: User[];
  isLoading: boolean;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
}

export function UserTable({
  users,
  isLoading,
  onEdit,
  onDelete,
}: UserTableProps) {
  const { t } = useTranslation(["user", "common"]);
  return (
    <div className="overflow-hidden rounded-lg border border-blue-600">
      <Table>
        <TableHeader className="bg-blue-600 text-white">
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.userName")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.email")}
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
          ) : users.length === 0 ? (
            <TableRow>
              <TableCell colSpan={6}>
                <EmptyState
                  title={t("list.emptyTitle")}
                  description={t("list.emptyDescription")}
                  icon={
                    <KeyRound className="text-muted-foreground mb-4 h-12 w-12" />
                  }
                />
              </TableCell>
            </TableRow>
          ) : (
            users.map((user) => (
              <TableRow key={user.id}>
                <TableCell className="text-center">{user.id}</TableCell>
                <TableCell className="text-center">{user.name}</TableCell>
                <TableCell className="text-center">{user.email}</TableCell>
                <TableCell className="text-center">
                  {formatISO(user.createdAt)}
                </TableCell>
                <TableCell className="text-center">
                  {formatISO(user.updatedAt)}
                </TableCell>
                <TableCell>
                  <div className="flex items-center justify-center gap-2">
                    <HasPermission perm={"PUT /users"}>
                      <Button
                        size="icon"
                        variant="ghost"
                        className="text-orange-500 hover:text-orange-600"
                        onClick={() => onEdit(user.id)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                    </HasPermission>

                    <HasPermission perm={"DELETE /users/{id}"}>
                      <DeleteConfirmDialog onConfirm={() => onDelete(user.id)}>
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
