import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/shared/presentation/ui/breadcrumb.tsx";
import { errorMessage } from "@/shared/domain/errors";
import { companies as companyUseCases, jobs as jobUseCases } from "@/app/container";
import type { Company } from "../../domain/company";
import type { Job } from "@/features/job/domain/job";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner";
import CompanySection from "../components/CompanySection.tsx";
import JobsSection from "../components/CompanyJobsSection.tsx";

type CompanyDetailsProp = {
  initialCompany?: Company;
};

const CompanyDetailsClientPage = ({ initialCompany }: CompanyDetailsProp) => {
  const [isLoading, setIsLoading] = useState(false);
  const [company, setCompany] = useState<Company | undefined>(
    initialCompany,
  );
  const [jobs, setJobs] = useState<Job[]>([]);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (!id) {
      navigate("/");
      return;
    }

    const fetchCompany = async () => {
      setIsLoading(true);
      try {
        const res = await companyUseCases.find(parseInt(id));
        const jobRes = await jobUseCases.ofCompany(parseInt(id));
        setCompany(res);
        setJobs(jobRes || []);
      } catch (err) {
        toast.error(errorMessage(err, "Không thể lấy thông tin công ty"));
      } finally {
        setIsLoading(false);
      }
    };

    if (!initialCompany) fetchCompany();
  }, [id, navigate, initialCompany]);

  if (isLoading) {
    return (
      <div className="flex h-[300px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!company) return null;

  return (
    <div
      className={`mx-auto my-12 ${jobs && jobs.length > 0 ? "w-4/5" : "w-3/5"}`}
    >
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink
              onClick={() => navigate("/companies")}
              className="cursor-pointer"
            >
              Danh sách công ty
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbPage>Công ty {company.name}</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

      <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
        <CompanySection company={company} jobsCount={jobs.length} />

        <JobsSection jobs={jobs} />
      </div>
    </div>
  );
};

export default CompanyDetailsClientPage;
