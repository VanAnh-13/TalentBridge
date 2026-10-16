import { useEffect, useState } from "react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/shared/presentation/ui/dialog.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Label } from "@/shared/presentation/ui/label.tsx";
import type { NewSkill, Skill, SkillChange } from "../../domain/skill";
import { DialogDescription } from "@radix-ui/react-dialog";
import { useTranslation } from "react-i18next";

interface SkillFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (data: NewSkill, id?: number) => void;
  initialData: Skill | null;
  onCloseForm: () => void;
  theme?: "blue" | "purple";
}

export function SkillForm({
  open,
  onOpenChange,
  onSubmit,
  initialData,
  onCloseForm,
  theme = "blue",
}: SkillFormProps) {
  const { t } = useTranslation(["skill", "common"]);
  const [formData, setFormData] = useState<
    NewSkill | SkillChange
  >({ name: "" });

  useEffect(() => {
    if (initialData)
      setFormData({ name: initialData.name, id: initialData.id });
    else setFormData({ name: "" });
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData, initialData?.id);
    setFormData({ name: "" });
    onOpenChange(false);
    onCloseForm?.();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-center">
            {t("form.title")}
          </DialogTitle>
          <DialogDescription className="text-center">
            {initialData ? t("form.editMode") : t("form.createMode")}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">
              {t("fields.name")} <span className="text-red-500">*</span>
            </Label>
            <Input
              id="name"
              required
              value={formData.name}
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, name: e.target.value }))
              }
            />
          </div>
          <div className="flex justify-end gap-2">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              {t("common:actions.cancel")}
            </Button>
            <Button
              type="submit"
              className={`${theme === "blue" ? "bg-blue-600 hover:bg-blue-700" : "bg-purple-600 hover:bg-purple-700"}`}
            >
              {initialData ? t("form.saveChanges") : t("common:actions.add")}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
