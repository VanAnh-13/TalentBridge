import type React from "react";
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
import RichTextEditor from "@/shared/presentation/components/RichText/index-editor";
import type { Company, CompanyData } from "../../domain/company";
import { DialogDescription } from "@radix-ui/react-dialog";
import { useTranslation } from "react-i18next";

interface CompanyFormProps {
  onSubmit: (data: CompanyData, logo: File | null, id?: number) => Promise<unknown>;
  initialData: Company | null;
  onCloseForm?: () => void;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  isRecruiter?: boolean;
  isLoading: boolean;
}

export function CompanyForm({
  open,
  onOpenChange,
  onSubmit,
  initialData,
  onCloseForm,
  isRecruiter = false,
  isLoading,
}: CompanyFormProps) {
  const { t } = useTranslation(["company", "common"]);
  const [formData, setFormData] = useState<CompanyData>({
    name: "",
    description: "",
    address: "",
  });
  const [logoPreview, setLogoPreview] = useState<string>("");
  const [logoFile, setLogoFile] = useState<File | null>(null);

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name,
        description: initialData.description,
        address: initialData.address,
      });
      setLogoPreview(initialData.logoUrl || "");
    } else {
      setFormData({ name: "", description: "", address: "" });
      setLogoPreview("");
    }
    setLogoFile(null);
  }, [initialData]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // SUBMIT
    await onSubmit(formData, logoFile, initialData?.id);

    // HANDLE AFTER SUBMIT
    if (!isRecruiter) handleReset();
    onOpenChange(false);
    onCloseForm?.();
  };

  const handleChange = (
    field: keyof CompanyData,
    value: string,
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleChangeLogo = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setLogoFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        if (typeof reader.result === "string") {
          setLogoPreview(reader.result);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleReset = () => {
    setFormData({ name: "", description: "", address: "" });
    setLogoPreview("");
    setLogoFile(null);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[80vh] overflow-y-auto sm:max-w-[600px]">
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
              {t("fields.name")}{" "}
              <span className="text-red-500">
                <span className="text-red-500">*</span>
              </span>
            </Label>
            <Input
              id="name"
              value={formData.name}
              onChange={(e) => handleChange("name", e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">
              {t("common:fields.description")} <span className="text-red-500">*</span>
            </Label>
            <RichTextEditor
              value={formData.description}
              onChange={(value) => {
                handleChange("description", value);
              }}
              placeholder={t("form.descriptionPlaceholder")}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="address">
              {t("common:fields.address")} <span className="text-red-500">*</span>
            </Label>
            <Input
              id="address"
              value={formData.address}
              onChange={(e) => handleChange("address", e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="logo">{t("common:fields.logo")}</Label>
            <Input
              id="logo"
              type="file"
              accept="image/*"
              onChange={handleChangeLogo}
            />
            {logoPreview && (
              <div className="mt-2">
                <img
                  src={logoPreview}
                  alt={t("form.logoPreview")}
                  className="h-16 rounded border"
                />
              </div>
            )}
          </div>

          <div className="flex justify-end gap-2">
            <Button
              type="button"
              variant="outline"
              disabled={isLoading}
              onClick={() => onOpenChange(false)}
            >
              {t("common:actions.cancel")}
            </Button>
            <Button
              disabled={isLoading}
              type="submit"
              className="bg-blue-600 hover:bg-blue-700"
            >
              {initialData ? t("form.saveChanges") : t("common:actions.add")}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
