import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import i18n from "i18next";
import { useSearchParams } from "react-router-dom";
import type { Job } from "../../domain/job";
import JobGrid from "../components/JobGrid";
import { JobSearchSection } from "../components/JobBrowseSearch";
import { errorMessage } from "@/shared/domain/errors";
import { jobs as jobUseCases } from "@/app/container";
import { toast } from "sonner";
import Pagination from "@/shared/presentation/components/Pagination";

/** The public job list. On arrival, `?name=` and `?location=` (sent by the home page search) prefill the filters. */
export default function JobClientPage() {
  const { t } = useTranslation("job");
  const [searchParams] = useSearchParams();
  // ============================
  // Data
  // ============================
  const [jobs, setJobs] = useState<Job[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // ============================
  // Pagination State
  // ============================
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(9);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // ============================
  // Search State
  // ============================
  // The location field is in the expanded part: open it when the URL brings a location.
  const [isExpandedSearch, setIsExpandedSearch] = useState(
    () => !!searchParams.get("location")?.trim(),
  );

  const [searchName, setSearchName] = useState(
    () => searchParams.get("name")?.trim() ?? "",
  );
  const [searchCompanyName, setsearchCompanyName] = useState("");
  const [searchLevel, setSearchLevel] = useState("all");
  const [searchLocation, setSearchLocation] = useState(
    () => searchParams.get("location")?.trim() ?? "",
  );

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchJobs = async (
    page: number,
    size: number,
    searchName: string,
    searchCompanyName: string,
    searchLevel: string,
    searchLocation: string,
  ) => {
    setIsLoading(true);
    try {
      const res = await jobUseCases.search(
        "all",
        {
          name: searchName,
          companyName: searchCompanyName,
          level: searchLevel,
          location: searchLocation,
        },
        page,
        size,
      );
      setJobs(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      // i18n.t rather than t: fetchJobs stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("browse.loadFailed", { ns: "job" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs(
      currentPage,
      itemsPerPage,
      searchName,
      searchCompanyName,
      searchLevel,
      searchLocation,
    );
  }, [
    currentPage,
    itemsPerPage,
    searchName,
    searchCompanyName,
    searchLevel,
    searchLocation,
  ]);

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setSearchName("");
    setsearchCompanyName("");
    setSearchLevel("all");
    setSearchLocation("");
    setIsExpandedSearch(false);

    fetchJobs(
      currentPage,
      itemsPerPage,
      searchName,
      searchCompanyName,
      searchLevel,
      searchLocation,
    );
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="relative w-full bg-gradient-to-r from-orange-500 via-orange-600 to-yellow-500 px-4 py-16">
        <div className="container mx-auto text-center">
          <div className="mb-6">
            <h1 className="mb-4 text-5xl font-bold text-white">
              {t("browse.title")}
            </h1>
            <p className="mx-auto max-w-2xl text-xl text-orange-100">
              {t("browse.subtitle")}
            </p>
          </div>

          <div className="mt-8 flex justify-center gap-8">
            <div className="text-center">
              <div className="text-2xl font-bold text-white">2000+</div>
              <div className="text-sm text-orange-200">
                {t("browse.statJobs")}
              </div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-white">500+</div>
              <div className="text-sm text-orange-200">
                {t("browse.statCompanies")}
              </div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-white">10k+</div>
              <div className="text-sm text-orange-200">
                {t("browse.statCandidates")}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto w-4/5">
        {/* Search Section */}
        <div className="my-12">
          <JobSearchSection
            searchName={searchName}
            searchCompanyName={searchCompanyName}
            searchLevel={searchLevel}
            searchLocation={searchLocation}
            isExpanded={isExpandedSearch}
            onReset={handleReset}
            onExpandToggle={() => setIsExpandedSearch(!isExpandedSearch)}
            onChange={{
              name: setSearchName,
              company: setsearchCompanyName,
              level: setSearchLevel,
              location: setSearchLocation,
            }}
          />
          <div className="mt-4 text-center text-sm text-gray-500">
            {t("browse.found", { count: jobs.length })}
          </div>
        </div>

        <JobGrid isLoading={isLoading} jobs={jobs} />

        <div className="my-12">
          <Pagination
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            totalPages={totalPages}
            totalElements={totalElements}
            itemsPerPage={itemsPerPage}
            setItemsPerPage={setItemsPerPage}
            showItemsPerPageSelect={false}
          />
        </div>
      </div>
    </div>
  );
}
