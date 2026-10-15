import type { Resume, StatusChange } from "../../domain/resume";
import { useEffect, useState } from "react";
import { ScheduleInterviewDialog } from "@/features/interview";
import { ResumeSearchSection } from "../components/ResumeSearchSection.tsx";
import Pagination from "@/shared/presentation/components/Pagination";
import { resumes as resumeUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { toast } from "sonner";
import { ResumeTable } from "../components/ResumeTable.tsx";
import { ViewResumeDialog } from "../components/ViewResumeDialog.tsx";

const ResumeManagerRecruiterPage = () => {
  const [inviting, setInviting] = useState<Resume | null>(null);
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
  ) => {
    setIsLoading(true);
    try {
      const res = await resumeUseCases.search(
        "company",
        { jobName: searchJobName },
        page,
        size,
      );
      setResumes(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể lấy danh sách công ty"));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchResumes(currentPage, itemsPerPage, searchJobName);
  }, [currentPage, itemsPerPage, searchJobName]);

  useEffect(() => {
    fetchResumes(1, itemsPerPage, searchJobName);
    setCurrentPage(1);
  }, [itemsPerPage, searchJobName]);

  // ============================
  // HANDLE UPDATE RESUME STATUS
  // ============================
  const handleUpdateResumeStatus = async (
    resume: StatusChange,
  ) => {
    setIsLoading(true);
    try {
      await resumeUseCases.changeStatus("company", resume);
      await fetchResumes(currentPage, itemsPerPage, searchJobName);
      toast.success("Cập nhật trạng thái hồ sơ thành công");
    } catch (err) {
      toast.error(errorMessage(err, "Cập nhật trạng thái hồ sơ thất bại"));
    } finally {
      setIsLoading(false);
    }
  };

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setSearchJobName("");
    setCurrentPage(1);
  };

  return (
    <div className="space-y-6">
      <ResumeSearchSection
        onReset={handleReset}
        searchJobName={searchJobName}
        setSearchJobName={setSearchJobName}
        isRecruiter={true}
      />

      {/* Header Section */}
      <h2 className="text-lg font-semibold">Danh sách hồ sơ xin việc</h2>

      <ResumeTable
        resumes={resumes}
        isLoading={isLoading}
        onViewResumePDF={handleViewResumeDialog}
        onScheduleInterview={(resume) => setInviting(resume)}
        theme={"purple"}
      />

      <Pagination
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsPerPage={itemsPerPage}
        setItemsPerPage={setItemsPerPage}
        showItemsPerPageSelect={true}
        theme={"purple"}
      />

      <ScheduleInterviewDialog
        open={inviting !== null}
        onOpenChange={(open) => !open && setInviting(null)}
        resumeId={inviting?.id}
        title={inviting ? `Mời phỏng vấn ${inviting.user.email}` : undefined}
      />

      <ViewResumeDialog
        open={isDialogOpen}
        onOpenChange={setisDialogOpen}
        onUpdate={handleUpdateResumeStatus}
        resume={selectedResume ?? ({} as Resume)}
        onCloseForm={() => setSelectedResume(null)}
        theme={"purple"}
      />
    </div>
  );
};

export default ResumeManagerRecruiterPage;
