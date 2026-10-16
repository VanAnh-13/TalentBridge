import { useState, useEffect } from "react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/shared/presentation/ui/dialog.tsx";
import { Label } from "@/shared/presentation/ui/label.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Search, Building2, X } from "lucide-react";
import { toast } from "sonner";
import { companies as companyUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import Pagination from "@/shared/presentation/components/Pagination.tsx";
import type { CompanyRef } from "../../domain/company";
import { EmptyState } from "@/shared/presentation/components/EmptyState.tsx";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import { DialogDescription } from "@radix-ui/react-dialog";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

interface CompanySelectionProps {
  selectedCompany: CompanyRef | undefined;
  addCompany: (company: CompanyRef) => void;
  removeCompany: () => void;
}

const CompanySelection = ({
  selectedCompany,
  addCompany,
  removeCompany,
}: CompanySelectionProps) => {
  const { t } = useTranslation(["company", "common"]);

  // ============================
  // Modal State
  // ============================
  const [showDialog, setShowDialog] = useState(false);
  const closeModal = () => setShowDialog(false);

  // ============================
  // Pagination State
  // ============================
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // ============================
  // Fetching State
  // ============================
  const [searchName, setsearchName] = useState("");
  const [companies, setCompanies] = useState<CompanyRef[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchCompanies = async (
    page: number,
    size: number,
    searchName: string,
  ) => {
    setIsLoading(true);
    try {
      const data = await companyUseCases.search({ name: searchName }, page, size);

      const mapped: CompanyRef[] = data.content.map(
        (company) => ({
          id: company.id,
          name: company.name,
          address: company.address,
          logoUrl: company.logoUrl,
        }),
      );

      setCompanies(mapped);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
      setIsLoading(false);
    } catch (err) {
      // i18n.t rather than t: fetchCompanies stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("errors.loadList", { ns: "company" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCompanies(currentPage, itemsPerPage, searchName);
  }, [currentPage, itemsPerPage, searchName]);

  // ============================
  // Handle Add Company
  // ============================
  const onAddCompany = (company: CompanyRef) => {
    addCompany(company);
    closeModal();
  };

  return (
    <div className="space-y-2">
      <Label>
        {t("common:fields.company")} <span className="text-red-500">*</span>
      </Label>

      <div className="rounded-md border bg-gray-50/50 p-3">
        {selectedCompany && (
          <div className="mb-3 flex min-h-[100px] items-center justify-between rounded-lg border border-gray-200 bg-white p-3 shadow-sm">
            <div className="flex items-center gap-3">
              {selectedCompany.logoUrl ? (
                <img
                  src={selectedCompany.logoUrl}
                  alt={t("logoAlt", { name: selectedCompany.name })}
                  className="h-16 w-16 rounded-lg border object-contain"
                />
              ) : (
                <div className="flex h-20 w-20 items-center justify-center rounded-lg bg-blue-100">
                  <Building2 className="h-10 w-10 text-blue-600" />
                </div>
              )}
              <div>
                <span className="font-semibold text-gray-900">
                  {selectedCompany.name}
                </span>
                <p className="text-xs text-gray-500">
                  {selectedCompany.address}
                </p>
              </div>
            </div>
            <Button
              variant="ghost"
              size="sm"
              className="h-8 w-8 rounded-full p-0 hover:bg-red-50 hover:text-red-600"
              onClick={removeCompany}
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        )}

        {!selectedCompany && (
          <div className="mb-3 flex min-h-[100px] w-full items-center justify-center rounded-lg border-2 border-dashed border-gray-300">
            <div>
              <Building2 className="mx-auto mb-2 h-8 w-8 text-gray-400" />
              <p className="text-sm text-gray-500">{t("selection.none")}</p>
            </div>
          </div>
        )}

        <Dialog open={showDialog} onOpenChange={setShowDialog}>
          <DialogTrigger asChild>
            <Button
              variant="outline"
              size="sm"
              className="w-full border-dashed"
            >
              <Building2 className="mr-2 h-4 w-4" />
              {selectedCompany ? t("selection.change") : t("selection.choose")}
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-lg">
            <DialogHeader>
              <DialogTitle className="flex items-center gap-2">
                <Building2 className="h-5 w-5" />
                {t("selection.choose")}
              </DialogTitle>
              <DialogDescription>{t("selection.description")}</DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="relative">
                <Search className="text-muted-foreground absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2" />
                <Input
                  placeholder={t("selection.searchPlaceholder")}
                  value={searchName}
                  onChange={(e) => setsearchName(e.target.value)}
                  className="pl-10"
                />
              </div>
              <div className="max-h-80 space-y-2 overflow-y-auto pr-2">
                {isLoading && (
                  <div className="flex justify-center py-6">
                    <LoadingSpinner />
                  </div>
                )}

                {companies.length === 0 && !isLoading && (
                  <div className="py-8 text-center">
                    <EmptyState
                      title={t("notFound.title")}
                      description={t("notFound.description")}
                      icon={
                        <Building2 className="text-muted-foreground h-12 w-12" />
                      }
                    />
                  </div>
                )}

                {companies.map((company) => (
                  <div
                    key={company.id}
                    className="group cursor-pointer rounded-lg border-2 p-4 transition-all duration-200 hover:shadow-md"
                    onClick={() => onAddCompany(company)}
                  >
                    <div className="flex items-center gap-3">
                      {company.logoUrl ? (
                        <img
                          src={company.logoUrl}
                          alt={t("logoAlt", { name: company.name })}
                          className="h-10 w-10 rounded-lg border object-contain"
                        />
                      ) : (
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-100">
                          <Building2 className="h-5 w-5 text-blue-600" />
                        </div>
                      )}
                      <div className="flex-1">
                        <h4 className="font-semibold break-all whitespace-normal">
                          {company.name}
                        </h4>
                        <p className="break-all whitespace-normal">
                          {company.address}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {!isLoading && totalPages > 1 && (
                <Pagination
                  currentPage={currentPage}
                  setCurrentPage={setCurrentPage}
                  totalPages={totalPages}
                  totalElements={totalElements}
                  itemsPerPage={itemsPerPage}
                  setItemsPerPage={setItemsPerPage}
                  showItemsPerPageSelect={false}
                />
              )}
            </div>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
};

export default CompanySelection;
