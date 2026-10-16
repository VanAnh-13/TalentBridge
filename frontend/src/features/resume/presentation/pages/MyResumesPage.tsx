import { useAuthState } from "@/features/auth";
import { FileText } from "lucide-react";
import { Card, CardContent } from "@/shared/presentation/ui/card";
import { useEffect, useState, useCallback } from "react";
import { useTranslation } from "react-i18next";
import { resumes as resumeUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { toast } from "sonner";
import Pagination from "@/shared/presentation/components/Pagination";
import ResumeCard from "../components/ResumeCard";
import type { Resume } from "../../domain/resume";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { EmptyState } from "@/shared/presentation/components/EmptyState";

export default function UserResumePage() {
  const { t } = useTranslation(["resume", "common"]);
  const { user } = useAuthState();

  // ===================
  // Data
  // ===================
  const [resumes, setResumes] = useState<Resume[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // ===================
  // Pagination
  // ===================
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // ===================
  // Handle Fetching
  // ===================
  const fetchResumes = useCallback(async () => {
    if (!user?.id) return;
    setIsLoading(true);
    try {
      const res = await resumeUseCases.mine(currentPage, itemsPerPage);

      setResumes(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      toast.error(errorMessage(err, t("list.loadFailed")));
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, itemsPerPage, user?.id, t]);

  useEffect(() => {
    fetchResumes();
  }, [fetchResumes]);

  // ===================
  // Handle Delete
  // ===================
  const handleDeleteResume = async (jobId: number) => {
    try {
      setIsLoading(true);
      await resumeUseCases.withdraw(jobId);
      toast.success(t("mine.withdrawn"));
      await fetchResumes();
    } catch (err) {
      toast.error(errorMessage(err, t("mine.withdrawFailed")));
    } finally {
      setIsLoading(false);
    }
  };

  // ===================
  // Handle Update Resume
  // ===================
  const handleUpdateResumeFile = async (resumeId: number, file: File) => {
    try {
      setIsLoading(true);
      await resumeUseCases.replaceFile(resumeId, file);
      await fetchResumes();
    } catch (err) {
      toast.error(errorMessage(err, t("mine.updateFailed")));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-5xl px-4 py-6">
      <h1 className="my-4 text-2xl font-bold text-gray-900">
        {t("mine.title")} {totalElements > 0 && `(${totalElements})`}
      </h1>

      {isLoading ? (
        <div className="mx-auto w-fit">
          <LoadingSpinner className="h-24 w-24" />
          <p className="my-2">{t("common:states.loading")}</p>
        </div>
      ) : resumes.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-8">
            <EmptyState
              title={t("mine.emptyTitle")}
              description={t("mine.emptyText")}
              icon={<FileText className="mb-4 h-12 w-12 text-gray-400" />}
            />
          </CardContent>
        </Card>
      ) : (
        <>
          <div className="grid grid-cols-1 gap-4">
            {resumes.map((resume) => (
              <ResumeCard
                key={resume.id}
                resume={resume}
                onDelete={handleDeleteResume}
                onUpdateResumeFile={handleUpdateResumeFile}
              />
            ))}
          </div>

          <div className="mt-6 flex justify-end">
            <Pagination
              currentPage={currentPage}
              setCurrentPage={setCurrentPage}
              totalPages={totalPages}
              totalElements={totalElements}
              itemsPerPage={itemsPerPage}
              setItemsPerPage={setItemsPerPage}
              showItemsPerPageSelect
            />
          </div>
        </>
      )}
    </div>
  );
}
