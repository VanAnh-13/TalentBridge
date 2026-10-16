import { Button } from "@/shared/presentation/ui/button";
import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";
import { useState } from "react";
import { useTranslation } from "react-i18next";

interface MemberInviteFromProps {
  onSubmit: (email: string) => void;
}

const MemberInviteFrom = ({ onSubmit }: MemberInviteFromProps) => {
  const { t } = useTranslation(["company", "common"]);
  const [userEmail, setUserEmail] = useState("");

  return (
    <div className="bg-card rounded-lg border p-4">
      <div className="grid grid-cols-1 items-end gap-4 md:grid-cols-3">
        <div className="space-y-2">
          <Label htmlFor="email-input">{t("members.invite.label")}</Label>
          <Input
            id="email-input"
            placeholder={t("members.invite.placeholder")}
            value={userEmail}
            onChange={(e) => setUserEmail(e.target.value)}
          />
        </div>
        <div className="flex gap-2">
          <Button
            className="bg-purple-500 hover:bg-purple-600"
            onClick={() => onSubmit(userEmail)}
          >
            {t("common:actions.add")}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default MemberInviteFrom;
