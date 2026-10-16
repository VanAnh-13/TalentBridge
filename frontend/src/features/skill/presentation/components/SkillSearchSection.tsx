import { RotateCcw } from "lucide-react";
import { Label } from "@/shared/presentation/ui/label.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { useTranslation } from "react-i18next";

interface SkillSearchSectionProps {
  searchName: string;
  setSearchName: (value: string) => void;
  onReset: () => void;
}

export function SkillSearchSection({
  searchName,
  setSearchName,
  onReset,
}: SkillSearchSectionProps) {
  const { t } = useTranslation(["skill", "common"]);
  return (
    <div className="rounded-lg border bg-card p-4">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
        <div className="space-y-2">
          <Label htmlFor="search-skill-name">{t("search.name")}</Label>
          <Input
            id="search-skill-name"
            placeholder={t("search.placeholder")}
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
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
