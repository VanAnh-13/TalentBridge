import { useAccount } from "@/features/auth";
import type React from "react";
import { useEffect, useState } from "react";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Label } from "@/shared/presentation/ui/label.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/presentation/ui/select.tsx";
import { Switch } from "@/shared/presentation/ui/switch.tsx";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/shared/presentation/ui/card.tsx";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/shared/presentation/ui/breadcrumb.tsx";
import { ArrowLeft } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import i18n from "i18next";
import RichTextEditor from "@/shared/presentation/components/RichText/index-editor.tsx";
import { SkillSelection } from "@/features/skill";
import type { SkillRef } from "@/features/skill/domain/skill";
import type { JobData, Level } from "../../domain/job";
import { errorMessage } from "@/shared/domain/errors";
import { toast } from "sonner";
import { jobs as jobUseCases } from "@/app/container";
import { formatISOToYMD } from "@/shared/presentation/format.ts";

const levels: Level[] = ["INTERN", "FRESHER", "MIDDLE", "SENIOR"];

export default function JobUpsertRecruiterPage() {
  const { permissions, companyId } = useAccount();
  // ============================
  // Checking is edit or create
  // ============================
  const navigate = useNavigate();
  const { t } = useTranslation(["job", "common"]);
  const [searchParams, setSearchParams] = useSearchParams();
  const id = searchParams.get("id");
  const isEdit = !!id;

  // ============================
  // Form data
  // ============================
  const [formData, setFormData] = useState<JobData>({
    name: "",
    location: "",
    salary: 0,
    quantity: 1,
    level: "INTERN",
    description: "",
    startDate: "",
    endDate: "",
    active: true,
    company: { id: parseInt(companyId) },
    skills: [],
  });

  const [selectedSkills, setSelectedSkills] = useState<SkillRef[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (id) {
      const fetchData = async () => {
        setIsLoading(true);
        try {
          const job = await jobUseCases.find(parseInt(id));
          job.startDate = formatISOToYMD(job.startDate);
          job.endDate = formatISOToYMD(job.endDate);

          setFormData(job);
          setSelectedSkills(job.skills);
        } catch (err) {
          // i18n.t rather than t: the effect keeps its dependencies, so switching language does not fetch again
          toast.error(errorMessage(err, i18n.t("upsert.notFound", { ns: "job" })));
          setSearchParams({});
        } finally {
          setIsLoading(false);
        }
      };

      fetchData();
    }
  }, [id, setSearchParams]);

  // ============================
  // Skill selection handler
  // ============================
  const addSkill = (skill: SkillRef) => {
    const exists = selectedSkills.some((s) => s.id === skill.id);
    if (exists) return;

    const updated = [...selectedSkills, skill];
    const skillIds = updated.map((s) => ({ id: s.id }));

    setSelectedSkills(updated);
    setFormData((form) => ({ ...form, skills: skillIds }));
  };

  const removeSkill = (skill: SkillRef) => {
    const updated = selectedSkills.filter((s) => s.id !== skill.id);
    const skillIds = updated.map((s) => ({ id: s.id }));

    setSelectedSkills(updated);
    setFormData((form) => ({ ...form, skills: skillIds }));
  };

  // ============================
  // General Field Handler
  // ============================
  const handleInputChange = (
    field: keyof JobData,
    value: string | number | boolean,
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const toISODate = (dateStr: string): string => {
    return new Date(dateStr).toISOString();
  };

  // ============================
  // Form Submit Handler
  // ============================

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      let newCopy = { ...formData };
      newCopy = {
        ...newCopy,
        startDate: toISODate(newCopy.startDate),
        endDate: toISODate(newCopy.endDate),
      };

      if (isEdit) await jobUseCases.update("company", parseInt(id), newCopy);
      else await jobUseCases.create("company", newCopy);

      toast.success(isEdit ? t("upsert.updated") : t("upsert.created"));
      handleBack();
    } catch (err) {
      toast.error(errorMessage(err, t("upsert.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  // ============================
  // Utils Handler
  // ============================
  const handleBack = () => {
    navigate("/recruiter/jobs");
  };

  useEffect(() => {
    if (isEdit && !permissions.includes("PUT /jobs/company/{id}"))
      navigate("/recruiter/job-manager");
    else if (!isEdit && !permissions.includes("POST /jobs/company"))
      navigate("/recruiter/job-manager");
  }, [isEdit, navigate, permissions]);

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink onClick={handleBack} className="cursor-pointer">
              {t("upsert.breadcrumb")}
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbPage>
              {isEdit ? t("upsert.breadcrumbEdit") : t("upsert.breadcrumbCreate")}
            </BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="outline" size="icon" onClick={handleBack}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h1 className="text-2xl font-bold">
            {isEdit ? t("upsert.titleEdit") : t("upsert.titleCreate")}
          </h1>
          <p className="text-muted-foreground">
            {isEdit ? t("upsert.subtitleEdit") : t("upsert.subtitleCreate")}
          </p>
        </div>
      </div>

      {/* Form */}
      <Card>
        <CardHeader>
          <CardTitle>{t("upsert.cardTitle")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="space-y-6">
            {/* Row 1 */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="name">
                  {t("upsert.name")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="name"
                  placeholder={t("search.namePlaceholder")}
                  value={formData.name}
                  onChange={(e) => handleInputChange("name", e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="location">
                  {t("fields.location")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="location"
                  placeholder={t("upsert.locationPlaceholder")}
                  value={formData.location}
                  onChange={(e) =>
                    handleInputChange("location", e.target.value)
                  }
                  required
                />
              </div>
            </div>

            {/* Row 2 */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
              <div className="space-y-2">
                <Label htmlFor="salary">
                  {t("fields.salary")} <span className="text-red-500">*</span>
                </Label>
                <div className="relative">
                  <Input
                    id="salary"
                    placeholder={t("upsert.salaryPlaceholder")}
                    type="number"
                    value={formData.salary}
                    onChange={(e) =>
                      handleInputChange("salary", Number(e.target.value))
                    }
                    required
                    className="appearance-none [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none"
                  />
                  <span className="text-muted-foreground absolute top-1/2 right-3 -translate-y-1/2">
                    VND
                  </span>
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="quantity">
                  {t("fields.quantity")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="quantity"
                  placeholder={t("upsert.quantityPlaceholder")}
                  type="number"
                  value={formData.quantity}
                  onChange={(e) =>
                    handleInputChange("quantity", Number(e.target.value))
                  }
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="level">
                  {t("fields.level")} <span className="text-red-500">*</span>
                </Label>
                <Select
                  value={formData.level}
                  onValueChange={(value) =>
                    handleInputChange("level", value as JobData["level"])
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder={t("upsert.levelPlaceholder")} />
                  </SelectTrigger>
                  <SelectContent>
                    {levels.map((level) => (
                      <SelectItem key={level} value={level}>
                        {t(`common:levels.${level}`)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Row 3 */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
              <div className="space-y-2">
                <Label htmlFor="startDate">
                  {t("fields.startDate")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="startDate"
                  type="date"
                  value={formData.startDate}
                  onChange={(e) =>
                    handleInputChange("startDate", e.target.value)
                  }
                  required
                  className="w-fit"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="endDate">
                  {t("fields.endDate")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="endDate"
                  type="date"
                  value={formData.endDate}
                  onChange={(e) => handleInputChange("endDate", e.target.value)}
                  required
                  className="w-fit"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="status">{t("common:fields.status")}</Label>
                <div className="flex items-center space-x-2">
                  <Switch
                    id="status"
                    checked={formData.active}
                    onCheckedChange={(checked) =>
                      handleInputChange("active", checked)
                    }
                  />
                  <Label htmlFor="status" className="text-sm font-medium">
                    {t(formData.active ? "common:states.active" : "common:states.inactive")}
                  </Label>
                </div>
              </div>
            </div>

            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">
                {t("upsert.description")} <span className="text-red-500">*</span>
              </Label>
              <RichTextEditor
                value={formData.description}
                onChange={(value) => handleInputChange("description", value)}
                placeholder={t("upsert.descriptionPlaceholder")}
              />
            </div>

            {/* Skills and Company Section */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              {/* Skills */}
              <SkillSelection
                selectedSkills={selectedSkills}
                onAddSkill={addSkill}
                onRemoveSkill={removeSkill}
              />
            </div>

            {/* Actions */}
            <div className="flex justify-end gap-4">
              <Button type="button" variant="outline" onClick={handleBack}>
                {t("common:actions.cancel")}
              </Button>
              <Button
                type="button"
                onClick={handleFormSubmit}
                disabled={isLoading}
                className="bg-blue-600 hover:bg-blue-700"
              >
                {isLoading
                  ? t("upsert.processing")
                  : isEdit
                    ? t("common:actions.update")
                    : t("manager.add")}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
