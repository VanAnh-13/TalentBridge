import { RotateCcw } from "lucide-react";
import { Label } from "@/shared/presentation/ui/label";
import { Input } from "@/shared/presentation/ui/input";
import { Button } from "@/shared/presentation/ui/button";
import { useTranslation } from "react-i18next";

interface RoleSearchSectionProps {
  searchRoleName: string;
  setSearchRoleName: (value: string) => void;
  onReset: () => void;
}

export function RoleSearchSection({
  searchRoleName,
  setSearchRoleName,
  onReset,
}: RoleSearchSectionProps) {
  const { t } = useTranslation(["access", "common"]);
  return (
    <div className="rounded-lg border bg-card p-4">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
        <div className="space-y-2">
          <Label htmlFor="search-role-name">{t("role.fields.name")}:</Label>
          <Input
            id="search-role-name"
            placeholder={t("role.search.namePlaceholder")}
            value={searchRoleName}
            onChange={(e) => setSearchRoleName(e.target.value)}
          />
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={onReset}>
            <RotateCcw className="h-4 w-4 mr-2" />
            {t("common:actions.reset")}
          </Button>
        </div>
      </div>
    </div>
  );
}
