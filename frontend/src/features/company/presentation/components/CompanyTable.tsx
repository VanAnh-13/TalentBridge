import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table";
import { Building2, Edit } from "lucide-react";
import { Button } from "@/shared/presentation/ui/button";
import type { Company } from "../../domain/company";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import { formatISO } from "@/shared/presentation/format.ts";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { HasPermission } from "@/features/auth";
import { Trans, useTranslation } from "react-i18next";

interface CompanyTableProps {
  companies: Company[];
  isLoading: boolean;
  hoveredCompany: Company | null;
  onViewDetails: (company: Company) => void;
  onEdit: (company: Company) => void;
  onDelete: (id: number) => void;
}

export function CompanyTable({
  companies,
  isLoading,
  hoveredCompany,
  onViewDetails,
  onEdit,
  onDelete,
}: CompanyTableProps) {
  const { t } = useTranslation(["company", "common"]);
  return (
    <div className="overflow-hidden rounded-lg border border-blue-600">
      <Table>
        <TableHeader className="bg-blue-600 text-white">
          <TableRow>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.id")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.logo")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("fields.name")}
            </TableHead>
            <TableHead className="w-[200px] text-center font-bold text-white">
              {t("common:fields.address")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.createdAt")}
            </TableHead>
            <TableHead className="text-center font-bold text-white">
              {t("common:fields.updatedAt")}
            </TableHead>
            <TableHead className="font-bold text-white">{t("common:fields.actions")}</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading && (
            <TableRow>
              <TableCell colSpan={6}>
                <div className="flex items-center justify-center py-8">
                  <LoadingSpinner />
                </div>
              </TableCell>
            </TableRow>
          )}

          {!isLoading && companies.length === 0 && (
            <TableRow>
              <TableCell colSpan={7}>
                <EmptyState
                  title={t("notFound.title")}
                  description={t("notFound.description")}
                  icon={
                    <Building2 className="text-muted-foreground h-12 w-12" />
                  }
                />
              </TableCell>
            </TableRow>
          )}

          {!isLoading &&
            companies.length > 0 &&
            companies.map((company) => {
              const isActive = hoveredCompany?.id === company.id;
              return (
                <TableRow
                  key={company.id}
                  className={`cursor-pointer transition-colors duration-200 ${
                    isActive ? "bg-muted/40" : "hover:bg-muted/50"
                  }`}
                  onClick={() => onViewDetails(company)}
                >
                  <TableCell className="text-center font-medium">
                    {company.id}
                  </TableCell>
                  <TableCell className="flex items-center justify-center font-medium">
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
                  </TableCell>
                  <TableCell className="w-[200px] text-center break-all whitespace-normal">
                    {company.name}
                  </TableCell>

                  <TableCell className="w-[200px] text-center break-all whitespace-normal">
                    {company.address}
                  </TableCell>
                  <TableCell className="text-muted-foreground text-center text-sm">
                    {formatISO(company.createdAt)}
                  </TableCell>
                  <TableCell className="text-muted-foreground text-center text-sm">
                    {formatISO(company.updatedAt)}
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center justify-center gap-2">
                      <HasPermission perm={"PUT /companies/{id}"}>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-orange-500 hover:text-orange-600"
                          onClick={(e) => {
                            e.stopPropagation();
                            onEdit(company);
                          }}
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                      </HasPermission>

                      <HasPermission perm={"DELETE /companies/{id}"}>
                        <DeleteConfirmDialog
                          onConfirm={() => onDelete(company.id)}
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
                        />
                      </HasPermission>
                    </div>
                  </TableCell>
                </TableRow>
              );
            })}
        </TableBody>
      </Table>
    </div>
  );
}
