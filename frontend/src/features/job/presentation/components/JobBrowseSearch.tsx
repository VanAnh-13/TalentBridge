"use client";

import { ChevronDown, ChevronUp, RotateCcw } from "lucide-react";
import { useTranslation } from "react-i18next";
import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";
import { Button } from "@/shared/presentation/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/presentation/ui/select";
import type { Level } from "../../domain/job";

const LEVELS: Level[] = ["INTERN", "FRESHER", "MIDDLE", "SENIOR"];

/**
 * Quick location filters. Each sets the city's name in the current language, the same names the home page
 * search suggests (app:home.search.cities), so both searches filter on the same text.
 */
const CITIES = ["hcm", "hanoi", "danang"] as const;

interface JobSearchSectionProps {
  searchName: string;
  searchCompanyName: string;
  searchLevel: string;
  searchLocation: string;
  isExpanded: boolean;
  onReset: () => void;
  onExpandToggle: () => void;
  onChange: {
    name: (val: string) => void;
    company: (val: string) => void;
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
    <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="search-title" className="font-medium text-gray-700">
            {t("search.name")}
          </Label>
          <Input
            id="search-title"
            placeholder={t("search.namePlaceholder")}
            value={searchName}
            onChange={(e) => onChange.name(e.target.value)}
            className="border-gray-300 focus:border-orange-500 focus:ring-orange-500"
          />
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={onReset}
            className="border-orange-500 bg-transparent text-orange-600 hover:bg-orange-50 hover:text-orange-700"
          >
            <RotateCcw className="mr-2 h-4 w-4" />
            {t("common:actions.reset")}
          </Button>
          <Button
            variant="outline"
            onClick={onExpandToggle}
            className="border-gray-300 bg-transparent text-gray-600 hover:bg-gray-50 hover:text-gray-700"
          >
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
            <div className="space-y-2">
              <Label
                htmlFor="search-company"
                className="font-medium text-gray-700"
              >
                {t("common:fields.company")}:
              </Label>
              <Input
                id="search-company"
                placeholder={t("search.companyPlaceholder")}
                value={searchCompanyName}
                onChange={(e) => onChange.company(e.target.value)}
                className="border-gray-300 focus:border-orange-500 focus:ring-orange-500"
              />
            </div>

            <div className="space-y-2">
              <Label
                htmlFor="search-level"
                className="font-medium text-gray-700"
              >
                {t("search.level")}
              </Label>
              <Select value={searchLevel} onValueChange={onChange.level}>
                <SelectTrigger className="border-gray-300 focus:border-orange-500 focus:ring-orange-500">
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
              <Label
                htmlFor="search-location"
                className="font-medium text-gray-700"
              >
                {t("search.location")}
              </Label>
              <div className="flex gap-2">
                <Input
                  id="search-location"
                  placeholder={t("search.locationPlaceholder")}
                  value={searchLocation}
                  onChange={(e) => onChange.location(e.target.value)}
                  className="flex-1 border-gray-300 focus:border-orange-500 focus:ring-orange-500"
                />
                <div className="flex gap-1">
                  {CITIES.map((city) => (
                    <Button
                      key={city}
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={() => onChange.location(t(`search.cities.${city}`))}
                      className="border-gray-300 text-xs text-gray-600 hover:border-orange-300 hover:bg-orange-50 hover:text-orange-600"
                    >
                      {t(`search.citiesShort.${city}`)}
                    </Button>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
