import { useState, useEffect } from "react";
import { X, Building2, MapPin, Calendar, Edit, Trash2 } from "lucide-react";
import { Button } from "@/shared/presentation/ui/button";
import { Badge } from "@/shared/presentation/ui/badge";
import { Separator } from "@/shared/presentation/ui/separator";
import type { Company } from "../../domain/company";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog";
import { formatISO } from "@/shared/presentation/format.ts";
import RichTextPreview from "@/shared/presentation/components/RichText/index-preview";
import { Trans, useTranslation } from "react-i18next";

interface CompanyDetailsSidebarProps {
  company: Company | null;
  isOpen: boolean;
  onClose: () => void;
  onEdit?: (company: Company) => void;
  onDelete?: (id: number) => void;
}

export function CompanyDetailsSidebar({
  company,
  isOpen,
  onClose,
  onEdit,
  onDelete,
}: CompanyDetailsSidebarProps) {
  const { t } = useTranslation(["company", "common"]);
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
        className={`fixed top-0 right-0 z-[101] h-full w-96 transform bg-white shadow-2xl transition-transform duration-300 ease-in-out ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
        style={{
          position: "fixed",
          height: "100vh",
          maxHeight: "100vh",
        }}
      >
        {company && (
          <div className="flex h-full flex-col">
            {/* Header */}
            <div className="flex items-center justify-between border-b bg-white p-6">
              <h2 className="text-lg font-semibold">{t("details.title")}</h2>
              <Button variant="ghost" size="icon" onClick={onClose}>
                <X className="h-4 w-4" />
              </Button>
            </div>

            {/* Content */}
            <div className="flex-1 space-y-6 overflow-y-auto bg-white p-6">
              {/* Company Logo & Name */}
              <div className="space-y-4 text-center">
                <div className="flex justify-center">
                  {company.logoUrl ? (
                    <img
                      src={company.logoUrl}
                      alt={t("logoAlt", { name: company.name })}
                      className="h-20 w-20 rounded-lg border object-contain"
                    />
                  ) : (
                    <div className="flex h-20 w-20 items-center justify-center rounded-lg bg-blue-100">
                      <Building2 className="h-10 w-10 text-blue-600" />
                    </div>
                  )}
                </div>
                <div>
                  <h3 className="text-xl font-bold text-gray-900">
                    {company.name}
                  </h3>
                  <Badge variant="secondary" className="mt-2">
                    {t("common:fields.id")}: {company.id}
                  </Badge>
                </div>
              </div>

              <Separator />

              {/* Company Info */}
              <div className="space-y-4">
                <div>
                  <label className="text-sm font-medium tracking-wide text-gray-500 uppercase">
                    {t("common:fields.address")}
                  </label>
                  <div className="mt-2 flex items-start gap-2">
                    <MapPin className="mt-0.5 h-4 w-4 flex-shrink-0 text-gray-400" />
                    <p className="text-gray-900">{company.address}</p>
                  </div>
                </div>

                {company.description && (
                  <div>
                    <label className="text-sm font-medium tracking-wide text-gray-500 uppercase">
                      {t("common:fields.description")}
                    </label>
                    <RichTextPreview content={company.description} />
                  </div>
                )}
              </div>

              <Separator />

              {/* Timestamps */}
              <div className="space-y-3">
                <div className="flex items-center gap-2 text-sm">
                  <Calendar className="h-4 w-4 text-gray-400" />
                  <span className="text-gray-500">{t("dates.created")}</span>
                  <span className="font-medium text-gray-900">
                    {formatISO(company.createdAt)}
                  </span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Calendar className="h-4 w-4 text-gray-400" />
                  <span className="text-gray-500">{t("dates.updated")}</span>
                  <span className="font-medium text-gray-900">
                    {formatISO(company.updatedAt)}
                  </span>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="border-t bg-white p-6">
              <div className="flex gap-3">
                <Button className="flex-1" onClick={() => onEdit?.(company)}>
                  <Edit className="mr-2 h-4 w-4" />
                  {t("common:actions.edit")}
                </Button>

                <DeleteConfirmDialog
                  onConfirm={() => onDelete?.(company.id)}
                  title={t("deleteDialog.title")}
                  styledDescription={
                    <p>
                      <Trans
                        t={t}
                        i18nKey="deleteDialog.description"
                        components={{ danger: <span className="text-red-500" /> }}
                      />
                    </p>
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
