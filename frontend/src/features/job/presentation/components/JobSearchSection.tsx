import { ChevronDown, ChevronUp, RotateCcw } from "lucide-react";
import { useTranslation } from "react-i18next";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Label } from "@/shared/presentation/ui/label.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/presentation/ui/select.tsx";
import type { Level } from "../../domain/job";

const LEVELS: Level[] = ["INTERN", "FRESHER", "MIDDLE", "SENIOR"];

interface JobSearchSectionProps {
  searchName: string;
  searchCompanyName?: string;
  searchLevel: string;
  searchLocation: string;
  isExpanded: boolean;
  onReset: () => void;
  onExpandToggle: () => void;
  onChange: {
    name: (val: string) => void;
    company?: (val: string) => void;
    level: (val: string) => void;
    location: (val: string) => void;
  };
}

export function JobSearchSection({
  searchName,
  searchCompanyName,
  searchLevel,
  searchLocation,
  isExpanded,
  onReset,
  onExpandToggle,
  onChange,
}: JobSearchSectionProps) {
  const { t } = useTranslation(["job", "common"]);
  return (
    <div className="bg-card rounded-lg border p-4">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="search-title">{t("search.name")}</Label>
          <Input
            id="search-title"
            placeholder={t("search.namePlaceholder")}
            value={searchName}
            onChange={(e) => onChange.name(e.target.value)}
          />
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={onReset}>
            <RotateCcw className="mr-2 h-4 w-4" />
            {t("common:actions.reset")}
          </Button>
          <Button variant="outline" onClick={onExpandToggle}>
            {isExpanded ? (
              <>
                <ChevronUp className="mr-2 h-4 w-4" />
                {t("common:actions.less")}
              </>
            ) : (
              <>
                <ChevronDown className="mr-2 h-4 w-4" />
                {t("common:actions.more")}
              </>
            )}
          </Button>
        </div>
      </div>

      {isExpanded && (
        <>
          <div className="my-4" />
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
            {typeof onChange.company === "function" && (
              <div className="space-y-2">
                <Label htmlFor="search-company">{t("common:fields.company")}:</Label>
                <Input
                  id="search-company"
                  placeholder={t("search.companyPlaceholder")}
                  value={searchCompanyName ?? ""}
                  onChange={(e) => onChange.company?.(e.target.value)}
                />
              </div>
            )}

            <div className="space-y-2">
              <Label htmlFor="search-level">{t("search.level")}</Label>
              <Select value={searchLevel} onValueChange={onChange.level}>
                <SelectTrigger>
                  <SelectValue placeholder={t("search.levelPlaceholder")} />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">{t("search.allLevels")}</SelectItem>
                  {LEVELS.map((level) => (
                    <SelectItem key={level} value={level}>
                      {t(`common:levels.${level}`)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="search-location">{t("search.location")}</Label>
              <Input
                id="search-location"
                placeholder={t("search.locationPlaceholder")}
                value={searchLocation}
                onChange={(e) => onChange.location(e.target.value)}
              />
            </div>
          </div>
        </>
      )}
    </div>
  );
}
