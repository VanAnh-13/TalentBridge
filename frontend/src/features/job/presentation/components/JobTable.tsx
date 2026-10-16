import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { Edit, Briefcase } from "lucide-react";
import { Trans, useTranslation } from "react-i18next";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog.tsx";
import { EmptyState } from "@/shared/presentation/components/EmptyState.tsx";
import { formatISO } from "@/shared/presentation/format.ts";
import type { Job } from "../../domain/job";
import { levelColors } from "@/shared/presentation/tagColors.ts";
import { HasPermission } from "@/features/auth";

interface JobTableProps {
  jobs: Job[];
  isLoading: boolean;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onView: (job: Job) => void;
  theme?: string;
}

export function JobTable({
  jobs,
  isLoading,
  onEdit,
  onDelete,
  onView,
  theme = "blue",
}: JobTableProps) {
  const { t } = useTranslation(["job", "common"]);
  const getStatusBadge = (active: boolean) => (
    <Badge variant={active ? "default" : "secondary"} className="capitalize">
      {active ? t("common:states.active") : t("common:states.inactive")}
    </Badge>
  );

  return (
    <div
      className={`overflow-hidden rounded-lg border ${theme === "blue" ? "border-blue-600" : "border-purple-600"}`}
    >
      <Table>
        <TableHeader
          className={`text-white ${theme === "blue" ? "bg-blue-600" : "bg-purple-600"}`}
        >
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.title")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.company")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.level")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.status")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.createdAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.endDate")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.actions")}
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading && (
            <TableRow>
              <TableCell colSpan={8}>
                <div className="flex items-center justify-center py-8">
                  <div
                    className={`h-8 w-8 animate-spin rounded-full border-b-2 ${theme === "blue" ? "border-blue-600" : "border-purple-600"}`}
                  ></div>
                </div>
              </TableCell>
            </TableRow>
          )}

          {!isLoading && jobs.length === 0 && (
            <TableRow>
              <TableCell colSpan={8}>
                <EmptyState
                  title={t("empty.title")}
                  description={t("empty.manage")}
                  icon={
                    <Briefcase className="text-muted-foreground h-12 w-12" />
                  }
                />
              </TableCell>
            </TableRow>
          )}

          {!isLoading &&
            jobs.map((job) => (
              <TableRow
                key={job.id}
                onClick={() => onView(job)}
                className="cursor-pointer transition-colors duration-200"
              >
                <TableCell className="text-center text-sm">{job.id}</TableCell>
                <TableCell className="text-center text-sm">
                  {job.name}
                </TableCell>
                <TableCell className="text-center text-sm">
                  {job.company.name}
                </TableCell>
                <TableCell className="text-center text-sm">
                  <Badge className={levelColors[job.level]}>{t(`common:levels.${job.level}`)}</Badge>
                </TableCell>
                <TableCell className="text-center text-sm">
                  {getStatusBadge(job.active)}
                </TableCell>
                <TableCell className="text-center text-sm">
                  {formatISO(job.startDate)}
                </TableCell>
                <TableCell className="text-center text-sm">
                  {formatISO(job.endDate)}
                </TableCell>
                <TableCell>
                  <div
                    className="flex items-center justify-center gap-2"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <HasPermission
                      perm={["PUT /jobs/{id}", "PUT /jobs/company/{id}"]}
                    >
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => onEdit(job.id)}
                      >
                        <Edit className="h-4 w-4 text-orange-500" />
                      </Button>
                    </HasPermission>

                    <HasPermission
                      perm={["DELETE /jobs/{id}", "DELETE /jobs/company/{id}"]}
                    >
                      <DeleteConfirmDialog
                        onConfirm={() => onDelete(job.id)}
                        title={t("delete.title")}
                        styledDescription={
                          <Trans
                            t={t}
                            i18nKey="delete.description"
                            components={{ warn: <span className="text-red-500" /> }}
                          />
                        }
                      />
                    </HasPermission>
                  </div>
                </TableCell>
              </TableRow>
            ))}
        </TableBody>
      </Table>
    </div>
  );
}
