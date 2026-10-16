import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";
import { Button } from "@/shared/presentation/ui/button";
import { RotateCcw } from "lucide-react";
import { useTranslation } from "react-i18next";

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
    <div className="bg-card rounded-lg border p-4">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-2 lg:grid-cols-4">
        <div className="space-y-2">
          <Label htmlFor="search-name">{t("search.name")}</Label>
          <Input
            id="search-name"
            placeholder={t("search.namePlaceholder")}
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="search-address">{t("search.address")}</Label>
          <Input
            id="search-address"
            placeholder={t("search.addressPlaceholder")}
            value={searchAddress}
            onChange={(e) => setSearchAddress(e.target.value)}
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
}
