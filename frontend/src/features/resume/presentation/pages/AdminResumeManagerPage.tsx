import type { Resume, StatusChange } from "../../domain/resume";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import i18n from "i18next";
import { ResumeSearchSection } from "../components/ResumeSearchSection.tsx";
import Pagination from "@/shared/presentation/components/Pagination.tsx";
import { resumes as resumeUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { toast } from "sonner";
import { ResumeTable } from "../components/ResumeTable.tsx";
import { ViewResumeDialog } from "../components/ViewResumeDialog.tsx";

const ResumeManagerPage = () => {
  const { t } = useTranslation("resume");
  // ============================
  // Data
  // ============================
  const [resumes, setResumes] = useState<Resume[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // ============================
  // Pagination State
  // ============================
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // ============================
  // Search State
  // ============================
  const [searchCompanyName, setsearchCompanyName] = useState("");
  const [searchJobName, setSearchJobName] = useState("");

  // ============================
  // Dialog State
  // ============================
  const [isDialogOpen, setisDialogOpen] = useState(false);
  const [selectedResume, setSelectedResume] =
    useState<Resume | null>(null);

  const handleViewResumeDialog = (resume: Resume) => {
    setSelectedResume(resume);
    setisDialogOpen(true);
  };

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchResumes = async (
    page: number,
    size: number,
    searchJobName: string,
    searchCompanyName: string,
  ) => {
    setIsLoading(true);
    try {
      const res = await resumeUseCases.search(
        "all",
        { jobName: searchJobName, companyName: searchCompanyName },
        page,
        size,
      );
      setResumes(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      // i18n.t rather than t: fetchResumes stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("list.loadFailed", { ns: "resume" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchResumes(currentPage, itemsPerPage, searchJobName, searchCompanyName);
  }, [currentPage, itemsPerPage, searchJobName, searchCompanyName]);

  useEffect(() => {
    fetchResumes(1, itemsPerPage, searchJobName, searchCompanyName);
    setCurrentPage(1);
  }, [itemsPerPage, searchJobName, searchCompanyName]);

  // ============================
  // HANDLE UPDATE RESUME STATUS
  // ============================
  const handleUpdateResumeStatus = async (
    resume: StatusChange,
  ) => {
    setIsLoading(true);
    try {
      await resumeUseCases.changeStatus("all", resume);
      await fetchResumes(
        currentPage,
        itemsPerPage,
        searchJobName,
        searchCompanyName,
      );
      toast.success(t("list.statusUpdated"));
    } catch (err) {
      toast.error(errorMessage(err, t("list.statusUpdateFailed")));
    } finally {
      setIsLoading(false);
    }
  };

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setsearchCompanyName("");
    setSearchJobName("");
    setCurrentPage(1);
  };

  return (
    <div className="space-y-6">
      <ResumeSearchSection
        searchCompanyName={searchCompanyName}
        setSearchCompanyName={setsearchCompanyName}
        searchJobName={searchJobName}
        setSearchJobName={setSearchJobName}
        onReset={handleReset}
      />

      {/* Header Section */}
      <h2 className="text-lg font-semibold">{t("list.title")}</h2>

      <ResumeTable
        resumes={resumes}
        isLoading={isLoading}
        onViewResumePDF={handleViewResumeDialog}
      />

      <Pagination
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsPerPage={itemsPerPage}
        setItemsPerPage={setItemsPerPage}
        showItemsPerPageSelect={true}
      />

      <ViewResumeDialog
        open={isDialogOpen}
        onOpenChange={setisDialogOpen}
        onUpdate={handleUpdateResumeStatus}
        resume={selectedResume ?? ({} as Resume)}
        onCloseForm={() => setSelectedResume(null)}
      />
    </div>
  );
};

export default ResumeManagerPage;
