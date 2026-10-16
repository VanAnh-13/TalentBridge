import { useEffect, useState } from "react";
import { Button } from "@/shared/presentation/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/shared/presentation/ui/dialog";
import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";
import { DialogDescription } from "@radix-ui/react-dialog";
import { Badge } from "@/shared/presentation/ui/badge";
import type { Permission, PermissionData } from "../../domain/permission";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/presentation/ui/select";
import { useTranslation } from "react-i18next";

const METHOD_COLORS: Record<string, string> = {
  GET: "bg-green-500",
  POST: "bg-blue-500",
  PUT: "bg-yellow-500",
  DELETE: "bg-red-500",
};

const MODULES = [
  "USER",
  "AUTH",
  "COMPANY",
  "SKILL",
  "JOB",
  "RESUME",
  "SUBSCRIBER",
  "ACCESS-CONTROLLER",
];
const METHODS = ["GET", "POST", "PUT", "DELETE"];

interface PermissionFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (data: PermissionData, id?: number) => void;
  initialData?: Permission | null;
  onCloseForm?: () => void;
}

export function PermissionForm({
  open,
  onOpenChange,
  onSubmit,
  initialData,
  onCloseForm,
}: PermissionFormProps) {
  const { t } = useTranslation(["access", "common"]);
  const [formData, setFormData] = useState<PermissionData>({
    name: "",
    apiPath: "",
    method: "",
    module: "",
  });

  useEffect(() => {
    if (initialData)
      setFormData({
        name: initialData.name,
        apiPath: initialData.apiPath,
        method: initialData.method,
        module: initialData.module,
      });
    else setFormData({ name: "", apiPath: "", method: "", module: "" });
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData, initialData?.id);
    setFormData({ name: "", apiPath: "", method: "", module: "" });
    onOpenChange(false);
    onCloseForm?.();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-center">
            {t("permission.form.title")}
          </DialogTitle>
          <DialogDescription className="text-center">
            {initialData ? t("permission.form.editDescription") : t("permission.form.createDescription")}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Name */}
          <div className="space-y-2">
            <Label htmlFor="name">
              {t("permission.form.name")} <span className="text-red-500">*</span>
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
          {/* API Path */}
          <div className="space-y-2">
            <Label htmlFor="apiPath">
              {t("permission.form.apiPath")} <span className="text-red-500">*</span>
            </Label>
            <Input
              id="apiPath"
              required
              value={formData.apiPath}
              placeholder="/users, /auth/login..."
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, apiPath: e.target.value }))
              }
            />
          </div>
          {/* Method, chosen by clicking a badge */}
          <div className="space-y-2">
            <Label>
              {t("permission.fields.method")} <span className="text-red-500">*</span>
            </Label>
            <div className="flex flex-wrap gap-2">
              {METHODS.map((m) => (
                <Badge
                  key={m}
                  className={`cursor-pointer px-2 py-1 text-base transition select-none ${METHOD_COLORS[m]} ${formData.method === m ? "scale-105 ring-2 ring-blue-500" : "opacity-60 hover:opacity-100"} `}
                  onClick={() =>
                    setFormData((prev) => ({ ...prev, method: m }))
                  }
                >
                  {m}
                </Badge>
              ))}
            </div>
            {/* Error while no method is chosen */}
            {!formData.method && (
              <span className="text-xs text-red-500">
                {t("permission.form.methodRequired")}
              </span>
            )}
          </div>
          {/* Module */}
          <div className="space-y-2">
            <Label>
              {t("permission.fields.module")} <span className="text-red-500">*</span>
            </Label>
            <Select
              value={formData.module}
              onValueChange={(value) =>
                setFormData((prev) => ({ ...prev, module: value }))
              }
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder={t("permission.form.modulePlaceholder")} />
              </SelectTrigger>
              <SelectContent>
                {MODULES.map((mod) => (
                  <SelectItem key={mod} value={mod}>
                    {mod}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* Buttons */}
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
              className="bg-blue-600 hover:bg-blue-700"
              disabled={!formData.method}
            >
              {initialData ? t("actions.saveChanges") : t("common:actions.add")}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
