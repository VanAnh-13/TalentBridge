import { RotateCcw } from "lucide-react";
import { Label } from "@/shared/presentation/ui/label.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { useTranslation } from "react-i18next";

interface UserSearchSectionProps {
  searchUserName: string;
  setSearchUserName: (value: string) => void;
  searchEmail: string;
  setSearchEmail: (value: string) => void;
  onReset: () => void;
}

export function UserSearchSection({
  searchUserName,
  setSearchUserName,
  searchEmail,
  setSearchEmail,
  onReset,
}: UserSearchSectionProps) {
  const { t } = useTranslation(["user", "common"]);
  return (
    <div className="bg-card rounded-lg border p-4">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="search-user-name">{t("fields.userName")}:</Label>
          <Input
            id="search-user-name"
            placeholder={t("search.userNamePlaceholder")}
            value={searchUserName}
            onChange={(e) => setSearchUserName(e.target.value)}
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="search-email">{t("common:fields.email")}:</Label>
          <Input
            id="search-email"
            placeholder={t("search.emailPlaceholder")}
            value={searchEmail}
            onChange={(e) => setSearchEmail(e.target.value)}
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
