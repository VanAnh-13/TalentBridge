import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";
import { Button } from "@/shared/presentation/ui/button";
import { RotateCcw } from "lucide-react";
import { useTranslation } from "react-i18next";

/** Shortcut buttons that fill in a city (labels and search terms: company:search.cities.<city>). */
const CITIES = ["hcm", "hanoi", "danang"] as const;

type Props = {
  searchName: string;
  setSearchName: (val: string) => void;
  searchAddress: string;
  setSearchAddress: (val: string) => void;
  onReset: () => void;
};

export function CompanySearchSection({
  searchName,
  setSearchName,
  searchAddress,
  setSearchAddress,
  onReset,
}: Props) {
  const { t } = useTranslation(["company", "common"]);
  return (
    <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <div className="mx-auto flex max-w-4xl flex-col items-end justify-center gap-4 lg:flex-row">
        <div className="min-w-0 flex-1 space-y-2">
          <Label htmlFor="search-name" className="font-medium text-gray-700">
            {t("search.name")}
          </Label>
          <Input
            id="search-name"
            placeholder={t("search.namePlaceholder")}
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            className="border-gray-300 focus:border-orange-500 focus:ring-orange-500"
          />
        </div>
        <div className="min-w-0 flex-1 space-y-2">
          <Label htmlFor="search-address" className="font-medium text-gray-700">
            {t("search.address")}
          </Label>
          <div className="flex gap-2">
            <Input
              id="search-address"
              placeholder={t("search.addressPlaceholder")}
              value={searchAddress}
              onChange={(e) => setSearchAddress(e.target.value)}
              className="flex-1 border-gray-300 focus:border-orange-500 focus:ring-orange-500"
            />
            <div className="flex gap-1">
              {CITIES.map((city) => (
                <Button
                  key={city}
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => setSearchAddress(t(`search.cities.${city}.value`))}
                  className="border-gray-300 text-xs text-gray-600 hover:border-orange-300 hover:bg-orange-50 hover:text-orange-600"
                >
                  {t(`search.cities.${city}.label`)}
                </Button>
              ))}
            </div>
          </div>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={onReset}
            className="border-orange-500 text-orange-600 hover:bg-orange-50 hover:text-orange-700"
          >
            <RotateCcw className="mr-2 h-4 w-4" />
            {t("common:actions.reset")}
          </Button>
        </div>
      </div>
    </div>
  );
}
