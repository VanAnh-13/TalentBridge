import { CalendarPlus, Edit, MessageCircle, Wrench } from "lucide-react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table.tsx";
import { EmptyState } from "@/shared/presentation/components/EmptyState.tsx";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import { formatISO } from "@/shared/presentation/format.ts";
import type { Resume } from "../../domain/resume";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@radix-ui/react-tooltip";
import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { getResumeStatusColor, statusOptions } from "@/shared/presentation/tagColors.ts";
import { useTranslation } from "react-i18next";

interface ResumeTableProps {
  resumes: Resume[];
  isLoading: boolean;
  onViewResumePDF: (resume: Resume) => void;
  /** Recruiters only: invite the candidate to an interview. */
  onScheduleInterview?: (resume: Resume) => void;
  /** Recruiters only: open the conversation with the candidate. */
  onChat?: (resume: Resume) => void;
  theme?: "blue" | "purple";
}

export function ResumeTable({
  resumes,
  isLoading,
  onViewResumePDF,
  onScheduleInterview,
  onChat,
  theme = "blue",
}: ResumeTableProps) {
  const { t } = useTranslation(["resume", "common"]);
  const statusLabel = (status: string) => {
    const known = statusOptions.find((option) => option.value === status);
    return known ? t(`common:resumeStatus.${known.value}`) : status;
  };
  return (
    <div className={`overflow-hidden rounded-lg border border-${theme}-600`}>
      <Table>
        <TableHeader
          className={`${theme === "blue" ? "bg-blue-600" : "bg-purple-600"} text-white`}
        >
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.logo")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.company")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.job")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("table.applicant")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("table.submittedAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.updatedAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.status")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.actions")}
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell colSpan={9}>
                <div className="flex justify-center py-6">
                  <LoadingSpinner />
                </div>
              </TableCell>
            </TableRow>
          ) : resumes.length === 0 ? (
            <TableRow>
              <TableCell colSpan={9}>
                <EmptyState
                  title={t("table.emptyTitle")}
                  description={t("table.emptyText")}
                  icon={
                    <Wrench className="text-muted-foreground mb-4 h-12 w-12" />
                  }
                />
              </TableCell>
            </TableRow>
          ) : (
            resumes.map((resume) => (
              <TableRow key={resume.id}>
                <TableCell className="text-center">{resume.id}</TableCell>
                <TableCell className="text-center">
                  {resume.company.logoUrl && (
                    <img
                      src={resume.company.logoUrl}
                      alt={resume.company.name}
                      className="h-14 w-14 rounded-md border bg-white object-contain shadow-sm"
                    />
                  )}
                </TableCell>
                <TableCell className="max-w-[200px] text-center break-all whitespace-normal">
                  {resume.company.name}
                </TableCell>
                <TableCell className="max-w-[200px] text-center break-all whitespace-normal">
                  {resume.job.name}
                </TableCell>
                <TableCell className="text-center">
                  {resume.user.email}
                </TableCell>
                <TableCell className="text-center">
                  {formatISO(resume.createdAt)}
                </TableCell>
                <TableCell className="text-center">
                  {formatISO(resume.updatedAt)}
                </TableCell>
                <TableCell className="text-center">
                  <Badge className={getResumeStatusColor(resume.status)}>
                    {statusLabel(resume.status)}
                  </Badge>
                </TableCell>
                <TableCell>
                  <div className="flex items-center justify-center">
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <Button
                            size="icon"
                            variant="ghost"
                            className="text-orange-500 hover:text-orange-600"
                            onClick={() => onViewResumePDF(resume)}
                          >
                            <Edit className="h-4 w-4" />
                          </Button>
                        </TooltipTrigger>
                        <TooltipContent>
                          <p>{t("table.viewCv")}</p>
                        </TooltipContent>
                      </Tooltip>
                      {onChat && (
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <Button
                              size="icon"
                              variant="ghost"
                              aria-label={t("table.message")}
                              className="text-blue-600 hover:text-blue-700"
                              onClick={() => onChat(resume)}
                            >
                              <MessageCircle className="h-4 w-4" />
                            </Button>
                          </TooltipTrigger>
                          <TooltipContent>
                            <p>{t("table.messageCandidate")}</p>
                          </TooltipContent>
                        </Tooltip>
                      )}
                      {onScheduleInterview && (
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <Button
                              size="icon"
                              variant="ghost"
                              aria-label={t("table.invite")}
                              className="text-purple-600 hover:text-purple-700"
                              onClick={() => onScheduleInterview(resume)}
                            >
                              <CalendarPlus className="h-4 w-4" />
                            </Button>
                          </TooltipTrigger>
                          <TooltipContent>
                            <p>{t("table.invite")}</p>
                          </TooltipContent>
                        </Tooltip>
                      )}
                    </TooltipProvider>
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
