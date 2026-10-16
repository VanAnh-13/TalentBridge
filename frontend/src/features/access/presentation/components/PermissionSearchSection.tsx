import { RotateCcw } from "lucide-react";
import { Label } from "@/shared/presentation/ui/label";
import { Input } from "@/shared/presentation/ui/input";
import { Button } from "@/shared/presentation/ui/button";
import { useTranslation } from "react-i18next";

interface PermissionSearchSectionProps {
  searchName: string;
  setSearchName: (value: string) => void;
  searchApiPath: string;
  setsearchApiPath: (value: string) => void;
  onReset: () => void;
}

export function PermissionSearchSection({
  searchName,
  setSearchName,
  searchApiPath,
  setsearchApiPath,
  onReset,
}: PermissionSearchSectionProps) {
  const { t } = useTranslation(["access", "common"]);
  return (
    <div className="bg-card rounded-lg border p-4">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="search-name">{t("permission.search.name")}</Label>
          <Input
            id="search-name"
            placeholder={t("permission.search.namePlaceholder")}
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="search-api-path">{t("permission.fields.apiPath")}:</Label>
          <Input
            id="search-api-path"
            placeholder={t("permission.search.apiPathPlaceholder")}
            value={searchApiPath}
            onChange={(e) => setsearchApiPath(e.target.value)}
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
