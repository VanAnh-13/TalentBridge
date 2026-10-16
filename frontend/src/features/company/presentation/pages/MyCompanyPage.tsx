import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { Button } from "@/shared/presentation/ui/button";
import { errorMessage } from "@/shared/domain/errors";
import { CompanyForm } from "../components/CompanyForm";
import CompanySection from "../components/CompanySection";
import { companies as companyUseCases } from "@/app/container";
import type { CompanyData } from "../../domain/company";
import type { Company } from "../../domain/company";
import { NotebookPen } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

const CompanyManagerRecruiterPage = () => {
  const { t } = useTranslation("company");
  const [isLoading, setIsLoading] = useState(false);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const [company, setCompany] = useState<
    Company | undefined
  >();

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchCompany = async () => {
    setIsLoading(true);
    try {
      const res = await companyUseCases.mine();
      setCompany(res ?? undefined);
    } catch (err) {
      // i18n.t rather than t: fetchCompany stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("errors.loadOne", { ns: "company" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCompany();
  }, []);

  // ============================
  // HANDLE UPDATE SELF COMPANY
  // ============================
  const handleUpdateSelfCompany = async (data: CompanyData, logo: File | null) => {
    try {
      setIsLoading(true);
      await companyUseCases.updateMine(data, logo);
      fetchCompany();
      toast.success(t("toasts.updated"));
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex h-[300px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!company) return null;

  return (
    <>
      <Button
        onClick={() => setIsFormOpen(true)}
        className="bg-purple-600 hover:bg-purple-700"
      >
        <NotebookPen className="mr-2 h-4 w-4" />
        {t("mine.edit")}
      </Button>

      <CompanySection company={company} jobsCount={0} isRecruiter={true} />

      <CompanyForm
        isLoading={isLoading}
        initialData={company}
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSubmit={handleUpdateSelfCompany}
      />
    </>
  );
};

export default CompanyManagerRecruiterPage;
