import { KeyRound, Trash2 } from "lucide-react";
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

import type { Member } from "../../domain/company";
import { Badge } from "@/shared/presentation/ui/badge";
import { useTranslation } from "react-i18next";

interface MemberTableProps {
  users: Member[];
  isLoading: boolean;
  onDelete: (email: string) => void;
  isOwner: boolean;
}

export function MemberTable({
  users,
  isLoading,
  onDelete,
  isOwner,
}: MemberTableProps) {
  const { t } = useTranslation(["company", "common"]);
  return (
    <div className="overflow-hidden rounded-lg border border-purple-600">
      <Table>
        <TableHeader className="bg-purple-600 text-white">
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("members.table.name")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.email")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.actions")}
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell colSpan={4}>
                <div className="flex justify-center py-6">
                  <LoadingSpinner />
                </div>
              </TableCell>
            </TableRow>
          ) : users.length === 0 ? (
            <TableRow>
              <TableCell colSpan={4}>
                <EmptyState
                  title={t("members.notFound.title")}
                  description={t("members.notFound.description")}
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
                <TableCell className="text-center">
                  {user.name} {user.owner && <Badge>OWNER</Badge>}
                </TableCell>
                <TableCell className="text-center">{user.email}</TableCell>
                <TableCell>
                  {isOwner && (
                    <div className="flex items-center justify-center gap-2">
                      {!user.owner && (
                        <DeleteConfirmDialog
                          onConfirm={() => onDelete(user.email)}
                        >
                          <Button
                            size="icon"
                            variant="ghost"
                            className="text-red-500 hover:text-red-600"
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </DeleteConfirmDialog>
                      )}
                    </div>
                  )}
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  );
}
