import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { jobs as jobUseCases } from "@/app/container";
import { toast } from "sonner";
import { errorMessage } from "@/shared/domain/errors";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import type { Job } from "../../domain/job";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/shared/presentation/ui/breadcrumb.tsx";
import JobSection from "../components/JobSection.tsx";
import { ApplySection } from "@/features/resume";
import { HasPermission } from "@/features/auth";

type JobDetailsProp = {
  initialJob?: Job;
};

const JobDetailsClientPage = ({ initialJob }: JobDetailsProp) => {
  const [isLoading, setIsLoading] = useState(false);
  const [job, setJob] = useState<Job | undefined>(initialJob);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (!id) {
      navigate("/");
      return;
    }

    const fetchJob = async () => {
      setIsLoading(true);
      try {
        const res = await jobUseCases.find(Number.parseInt(id));
        setJob(res);
      } catch (err) {
        toast.error(errorMessage(err, "Không thể lấy thông tin công việc"));
      } finally {
        setIsLoading(false);
      }
    };

    if (!initialJob) fetchJob();
  }, [id, navigate, initialJob]);

  if (isLoading) {
    return (
      <div className="flex h-[300px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!job) return null;

  return (
    <div className="mx-auto my-12 w-3/5">
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink
              onClick={() => navigate("/jobs")}
              className="cursor-pointer"
            >
              Danh sách việc làm
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbPage>{job.name}</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

      <div className="grid grid-cols-1 gap-8">
        <JobSection job={job} />
      </div>

      <HasPermission perm={"POST /resumes"}>
        <ApplySection jobId={job.id} jobTitle={job.name} />
      </HasPermission>
    </div>
  );
};

export default JobDetailsClientPage;
