import { Edit, Trash2, Wrench } from "lucide-react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table.tsx";
import type { Skill } from "../../domain/skill";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog.tsx";
import { EmptyState } from "@/shared/presentation/components/EmptyState.tsx";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import { formatISO } from "@/shared/presentation/format.ts";
import { HasPermission } from "@/features/auth";
import { useTranslation } from "react-i18next";

interface SkillTableProps {
  skills: Skill[];
  isLoading: boolean;
  onEdit: (skill: Skill) => void;
  onDelete: (id: number) => void;
  theme?: "blue" | "purple";
}

export function SkillTable({
  skills,
  isLoading,
  onEdit,
  onDelete,
  theme = "blue",
}: SkillTableProps) {
  const { t } = useTranslation(["skill", "common"]);
  return (
    <div
      className={`overflow-hidden rounded-lg border ${theme === "blue" ? "border-blue-600" : "border-purple-600"}`}
    >
      <Table>
        <TableHeader
          className={`${theme === "blue" ? "bg-blue-600" : "bg-purple-600"} text-white`}
        >
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.name")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.createdAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.updatedAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.actions")}
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell colSpan={5}>
                <div className="flex justify-center py-6">
                  <LoadingSpinner />
                </div>
              </TableCell>
            </TableRow>
          ) : skills.length === 0 ? (
            <TableRow>
              <TableCell colSpan={5}>
                <EmptyState
                  title={t("notFound.title")}
                  description={t("notFound.description")}
                  icon={
                    <Wrench className="text-muted-foreground mb-4 h-12 w-12" />
                  }
                />
              </TableCell>
            </TableRow>
          ) : (
            skills.map((skill) => (
              <TableRow key={skill.id}>
                <TableCell className="text-center">{skill.id}</TableCell>
                <TableCell className="text-center">{skill.name}</TableCell>
                <TableCell className="text-center">
                  {formatISO(skill.createdAt)}
                </TableCell>
                <TableCell className="text-center">
                  {formatISO(skill.updatedAt)}
                </TableCell>
                <TableCell>
                  <div className="flex items-center justify-center gap-2">
                    <HasPermission perm={"PUT /skills"}>
                      <Button
                        size="icon"
                        variant="ghost"
                        className="text-orange-500 hover:text-orange-600"
                        onClick={() => onEdit(skill)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                    </HasPermission>

                    <HasPermission perm={"DELETE /skills/{id}"}>
                      <DeleteConfirmDialog onConfirm={() => onDelete(skill.id)}>
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
