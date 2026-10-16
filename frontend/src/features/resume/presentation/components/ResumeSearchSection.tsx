import { RotateCcw } from "lucide-react";
import { Label } from "@/shared/presentation/ui/label.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { useTranslation } from "react-i18next";

interface ResumeSearchSectionProps {
  onReset: () => void;
  searchJobName: string;
  setSearchJobName: (value: string) => void;
  searchCompanyName?: string;
  setSearchCompanyName?: (value: string) => void;
  isRecruiter?: boolean;
}

export const ResumeSearchSection = ({
  onReset,
  searchJobName,
  setSearchJobName,
  searchCompanyName,
  setSearchCompanyName,
  isRecruiter = false,
}: ResumeSearchSectionProps) => {
  const { t } = useTranslation(["resume", "common"]);
  return (
    <div className="bg-card rounded-lg border p-4">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-3">
        {!isRecruiter && setSearchCompanyName && (
          <div className="space-y-2">
            <Label htmlFor="search-company-name">{t("search.companyName")}:</Label>
            <Input
              id="search-company-name"
              placeholder={t("search.companyPlaceholder")}
              value={searchCompanyName}
              onChange={(e) => setSearchCompanyName(e.target.value)}
            />
          </div>
        )}
        <div className="space-y-2">
          <Label htmlFor="search-job-name">{t("search.jobName")}:</Label>
          <Input
            id="search-job-name"
            placeholder={t("search.jobPlaceholder")}
            value={searchJobName}
            onChange={(e) => setSearchJobName(e.target.value)}
          />
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={onReset}>
            <RotateCcw className="mr-2 h-4 w-4" />
            {t("common:actions.reset")}
          </Button>
        </div>
      </div>
    </div>
  );
};
