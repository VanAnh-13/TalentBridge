import type { Company } from "../../domain/company";
import { Building2, MapPin } from "lucide-react";
import { Card, CardContent } from "@/shared/presentation/ui/card";
import { getFirstLineFromHtml } from "@/shared/presentation/format.ts";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

type CompanyGridProps = {
  isLoading: boolean;
  companies: Company[];
};

const CompanyGrid = ({ isLoading, companies }: CompanyGridProps) => {
  const navigate = useNavigate();
  const { t } = useTranslation("company");

  return (
    <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
      {isLoading && (
        <div className="col-span-3 flex min-h-[300px] items-center justify-center">
          <LoadingSpinner />
        </div>
      )}

      {!isLoading && companies.length === 0 && (
        <div className="col-span-3 flex min-h-[300px] items-center justify-center">
          <EmptyState
            title={t("notFound.title")}
            description={t("notFound.description")}
            icon={<Building2 className="text-muted-foreground h-12 w-12" />}
          />
        </div>
      )}

      {companies.map((company) => (
        <Card
          key={company.id}
          className="cursor-pointer bg-white transition-shadow duration-200 hover:shadow-lg"
          onClick={() => navigate(`/companies/${company.id}`)}
        >
          <CardContent className="p-6">
            <div className="mb-4 flex items-start justify-between">
              <div className="flex min-w-0 flex-1 items-center gap-3">
                <div className="flex h-16 w-16 flex-shrink-0 items-center justify-center overflow-hidden bg-gray-100">
                  {company.logoUrl ? (
                    <img
                      src={company.logoUrl || "/placeholder.svg"}
                      alt={t("logoAlt", { name: company.name })}
                      className="h-full w-full object-contain"
                    />
                  ) : (
                    <Building2 className="h-6 w-6 text-gray-600" />
                  )}
                </div>

                <div className="min-w-0 flex-1">
                  <h3 className="text-md line-clamp-2 font-semibold text-gray-900">
                    {company.name}
                  </h3>
                  <div className="flex items-center gap-1 text-sm text-gray-600">
                    <MapPin className="h-4 w-4 flex-shrink-0" />
                    <span className="truncate">{company.address}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Description */}
            <p className="mb-4 line-clamp-2 min-h-[2.5rem] text-sm text-gray-600">
              {getFirstLineFromHtml(company.description) || company.name}
            </p>

            <div className="font-medium text-orange-600">
              {t("grid.jobs", { count: company.jobsCount || 0 })}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

export default CompanyGrid;
