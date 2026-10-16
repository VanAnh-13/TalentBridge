import { SidebarProvider } from "@/shared/presentation/ui/sidebar";
import { Link, Outlet } from "react-router-dom";
import { RecruiterSidebar } from "./Sidebar";
import { RecruiterTopBar } from "./Topbar";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { errorMessage } from "@/shared/domain/errors";
import { companies as companyUseCases } from "@/app/container";
import type { CompanyData } from "@/features/company/domain/company";
import { CompanyForm } from "@/features/company";
import { Button } from "@/shared/presentation/ui/button";
import { Building2 } from "lucide-react";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

const RecruiterPage = () => {
  const { t } = useTranslation("app");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isChecking, setIsChecking] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [hasCompany, setHasCompany] = useState(false);

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchCompany = async () => {
    setIsChecking(true);
    try {
      const res = await companyUseCases.mine();
      if (res) setHasCompany(true);
    } catch (err) {
      // i18n.t rather than t: fetchCompany stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("recruiter.company.loadFailed", { ns: "app" })));
    } finally {
      setIsChecking(false);
    }
  };

  useEffect(() => {
    fetchCompany();
  }, []);

  const handleSelfCreateCompany = async (data: CompanyData, logo: File | null) => {
    try {
      setIsLoading(true);
      await companyUseCases.createMine(data, logo);
      fetchCompany();
      toast.success(t("recruiter.company.created"));
    } catch (err) {
      toast.error(errorMessage(err, t("recruiter.company.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  if (isChecking) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-tr from-purple-50 via-white to-purple-100">
        <div className="flex flex-col items-center gap-2">
          <span className="loader mb-2 h-10 w-10 animate-spin rounded-full border-4 border-purple-200 border-t-purple-600" />
          <span className="text-lg text-purple-700">
            {t("recruiter.company.loading")}
          </span>
        </div>
      </div>
    );
  }

  if (!hasCompany) {
    return (
      <>
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-tr from-purple-50 via-white to-purple-100">
          <div className="animate-fade-in flex flex-col items-center gap-4 rounded-xl border border-purple-200 bg-white/80 px-8 py-10 shadow-2xl">
            <Building2 className="mb-2 h-12 w-12 text-purple-500" />
            <h1 className="text-2xl font-bold text-gray-800">
              {t("recruiter.company.noneTitle")}
            </h1>
            <p className="mb-2 text-center text-gray-500">
              {t("recruiter.company.noneText")}
            </p>
            <Button
              className="rounded-lg bg-purple-600 px-8 py-2 font-semibold text-white shadow transition hover:bg-purple-700"
              onClick={() => setIsFormOpen(true)}
            >
              {t("recruiter.company.create")}
            </Button>
            <Link to={"/"} className="text-sm text-gray-500 hover:underline">
              {t("nav.backHome")}
            </Link>
          </div>
        </div>
        <CompanyForm
          initialData={null}
          open={isFormOpen}
          onOpenChange={setIsFormOpen}
          onSubmit={handleSelfCreateCompany}
          isLoading={isLoading}
        />
      </>
    );
  }

  return (
    <SidebarProvider>
      <div className="flex min-h-screen w-full">
        <RecruiterSidebar />
        <div className="flex flex-1 flex-col">
          <RecruiterTopBar />
          <div className="flex-1 p-4">
            <Outlet />
          </div>
        </div>
      </div>
    </SidebarProvider>
  );
};

export default RecruiterPage;
