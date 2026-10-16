import { EmptyState } from "@/shared/presentation/components/EmptyState";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { errorMessage } from "@/shared/domain/errors";
import { companies as companyUseCases } from "@/app/container";
import type { Company } from "../../domain/company";
import { Building2 } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

const TopCompaniesSection = () => {
  const { t } = useTranslation("company");
  const [companies, setCompanies] = useState<Company[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchCompanies = async () => {
    setIsLoading(true);
    try {
      const res = await companyUseCases.featured();
      setCompanies(res.content);
    } catch (err) {
      // i18n.t rather than t: fetchCompanies stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("errors.loadList", { ns: "company" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCompanies();
  }, []);

  return (
    <section className="bg-white px-4 py-16">
      <div className="mx-auto mb-10 max-w-7xl text-center">
        <h2 className="text-3xl font-bold text-gray-900">
          {t("top.title")}
        </h2>
        <p className="mt-2 text-gray-600">
          {t("top.subtitle")}
        </p>
      </div>

      <div className="mx-auto grid max-w-7xl grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5">
        {isLoading && (
          <div className="col-span-5 flex items-center justify-center">
            <LoadingSpinner />
          </div>
        )}

        {!isLoading && companies.length === 0 && (
          <div className="col-span-5 flex items-center justify-center">
            <EmptyState
              title={t("notFound.title")}
              description={t("top.emptyDescription")}
              icon={<Building2 className="text-muted-foreground h-12 w-12" />}
            />
          </div>
        )}

        {!isLoading &&
          companies.length > 0 &&
          companies.map((company) => (
            <div
              key={company.id}
              className="rounded-xl border p-5 text-center transition hover:shadow-md"
            >
              <img
                src={company.logoUrl}
                alt={company.name}
                className="mx-auto mb-3 h-16 w-16 object-contain"
              />
              <h3 className="mb-1 h-[100px] text-lg font-semibold text-gray-800">
                {company.name}
              </h3>

              <Link
                to={`/companies/${company.id}`}
                className="inline-block text-sm font-medium text-orange-600 hover:underline"
              >
                {t("top.viewJobs")}
              </Link>
            </div>
          ))}
      </div>
    </section>
  );
};

export default TopCompaniesSection;
