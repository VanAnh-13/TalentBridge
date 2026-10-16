import { useEffect, useState } from "react";
import { Trans, useTranslation } from "react-i18next";
import {
  X,
  MapPin,
  Calendar,
  Edit,
  Trash2,
  BadgeDollarSign,
  Layers3,
  Users,
  Building2,
} from "lucide-react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { Separator } from "@/shared/presentation/ui/separator.tsx";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog.tsx";
import { formatISO, formatSalary } from "@/shared/presentation/format.ts";
import type { Job } from "../../domain/job";
import RichTextPreview from "@/shared/presentation/components/RichText/index-preview.tsx";

interface JobDetailsSidebarProps {
  job: Job | null;
  isOpen: boolean;
  onClose: () => void;
  onEdit?: (job: Job) => void;
  onDelete?: (id: number) => void;
}

export function JobDetailsSidebar({
  job,
  isOpen,
  onClose,
  onEdit,
  onDelete,
}: JobDetailsSidebarProps) {
  const { t } = useTranslation(["job", "common"]);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsVisible(true);
      document.body.style.overflow = "hidden";
    } else {
      const timer = setTimeout(() => setIsVisible(false), 300);
      document.body.style.overflow = "unset";
      return () => clearTimeout(timer);
    }

    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  if (!isVisible) return null;

  return (
    <>
      {/* Backdrop */}
      <div
        className={`z-[100] bg-black/30 transition-opacity duration-300 ${
          isOpen ? "opacity-100" : "opacity-0"
        }`}
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          width: "100vw",
          height: "100vh",
        }}
        onClick={onClose}
      />

      {/* Sidebar */}
      <div
        className={`fixed top-0 right-0 z-[101] h-screen w-96 transform bg-white shadow-2xl transition-transform duration-300 ease-in-out ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        {job && (
          <div className="flex h-full flex-col">
            {/* Header */}
            <div className="flex items-center justify-between border-b bg-white p-6">
              <h2 className="text-lg font-semibold">{t("sidebar.title")}</h2>
              <Button variant="ghost" size="icon" onClick={onClose}>
                <X className="h-4 w-4" />
              </Button>
            </div>

            {/* Content */}
            <div className="flex-1 space-y-6 overflow-y-auto bg-white p-6">
              {/* Job Title */}
              <div className="space-y-3 text-center">
                <h3 className="text-xl font-bold text-gray-900">{job.name}</h3>
                <Badge variant="secondary">
                  {t("common:fields.id")}: {job.id}
                </Badge>
              </div>

              <Separator />

              {/* Company */}
              <div>
                <div className="my-4">
                  <label className="text-sm font-medium tracking-wide text-gray-500 uppercase">
                    {t("common:fields.company")}
                  </label>
                </div>
                <div className="mt-2 flex items-center gap-2">
                  <div>
                    {job.company.logoUrl ? (
                      <img
                        src={job.company.logoUrl}
                        alt={`${job.company.name} logo`}
                        className="h-10 w-10 rounded-lg border object-contain"
                      />
                    ) : (
                      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-100">
                        <Building2 className="h-5 w-5 text-blue-600" />
                      </div>
                    )}
                  </div>
                  <div>{job.company.name}</div>
                </div>
              </div>

              {/* Skills */}
              {job.skills.length > 0 && (
                <>
                  <div className="my-4">
                    <label className="text-sm font-medium tracking-wide text-gray-500 uppercase">
                      {t("fields.requiredSkills")}
                    </label>
                  </div>

                  <div className="my-4">
                    <div className="mt-2 flex flex-wrap gap-2">
                      {job.skills.map((skill) => (
                        <Badge key={skill.id} variant="outline">
                          {skill.name}
                        </Badge>
                      ))}
                    </div>
                  </div>
                </>
              )}

              {/* Job Info */}
              <div className="my-4">
                <label className="text-sm font-medium tracking-wide text-gray-500 uppercase">
                  {t("sidebar.general")}
                </label>
              </div>

              <div className="space-y-4">
                <div className="flex items-start gap-2">
                  <MapPin className="mt-1 h-4 w-4 text-gray-400" />
                  <p className="text-gray-900">{job.location}</p>
                </div>

                <div className="flex items-center gap-2">
                  <BadgeDollarSign className="h-4 w-4 text-gray-400" />
                  <p className="text-gray-900">{formatSalary(job.salary)}</p>
                </div>

                <div className="flex items-center gap-2">
                  <Users className="h-4 w-4 text-gray-400" />
                  <p className="text-gray-900">
                    {t("sidebar.quantity", { count: job.quantity })}
                  </p>
                </div>

                <div className="flex items-center gap-2">
                  <Layers3 className="h-4 w-4 text-gray-400" />
                  <p className="text-gray-900">
                    {t("sidebar.level", { level: t(`common:levels.${job.level}`) })}
                  </p>
                </div>
              </div>

              {/* Description */}

              <div className="my-4">
                <label className="text-sm font-medium tracking-wide text-gray-500 uppercase">
                  {t("common:fields.description")}
                </label>
              </div>
              <RichTextPreview content={job.description} />

              <Separator />

              {/* Dates */}
              <div className="space-y-2 text-sm">
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-gray-400" />
                  <span className="text-gray-500">{t("sidebar.startDate")}</span>
                  <span className="font-medium text-gray-900">
                    {formatISO(job.startDate)}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-gray-400" />
                  <span className="text-gray-500">{t("sidebar.endDate")}</span>
                  <span className="font-medium text-gray-900">
                    {formatISO(job.endDate)}
                  </span>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="border-t bg-white p-6">
              <div className="flex gap-3">
                <Button className="flex-1" onClick={() => onEdit?.(job)}>
                  <Edit className="mr-2 h-4 w-4" />
                  {t("common:actions.edit")}
                </Button>

                <DeleteConfirmDialog
                  onConfirm={() => onDelete?.(job.id)}
                  title={t("delete.title")}
                  styledDescription={
                    <Trans
                      t={t}
                      i18nKey="delete.description"
                      components={{ warn: <span className="text-red-500" /> }}
                    />
                  }
                >
                  <Button variant="destructive" className="flex-1">
                    <Trash2 className="mr-2 h-4 w-4" />
                    {t("common:actions.delete")}
                  </Button>
                </DeleteConfirmDialog>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
