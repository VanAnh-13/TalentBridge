import { useAccount } from "@/features/auth";
import type React from "react";

import { useEffect, useState } from "react";
import { Button } from "@/shared/presentation/ui/button";
import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/presentation/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/presentation/ui/card";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/shared/presentation/ui/breadcrumb";
import { ArrowLeft, Eye, EyeOff } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { errorMessage } from "@/shared/domain/errors";
import { toast } from "sonner";
import { users as userUseCases } from "@/app/container";
import type { CompanyRef } from "@/features/company";
import { CompanySelection } from "@/features/company";
import { RoleSelection } from "@/features/access";
import type { RoleRef } from "@/features/access";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

export default function UserUpsertPage() {
  const { t } = useTranslation(["user", "common"]);
  const { permissions } = useAccount();

  // ============================
  // Checking is edit or create
  // ============================
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const id = searchParams.get("id");
  const isEdit = !!id;

  // ============================
  // Form data
  // ============================
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    dob: "",
    address: "",
    gender: "OTHER" as "MALE" | "FEMALE" | "OTHER",
    companyId: "",
    roleId: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(false);

  const [selectedCompany, setSelectedCompany] = useState<
    CompanyRef | undefined
  >();
  const [selectedRole, setSelectedRole] = useState<RoleRef | undefined>();

  useEffect(() => {
    if (id) {
      const fetchData = async () => {
        setIsLoading(true);
        try {
          const user = await userUseCases.find(parseInt(id));
          setFormData({
            name: user.name,
            email: user.email,
            password: "",
            dob: user.dob ? user.dob.split("T")[0] : "",
            address: user.address ? user.address : "",
            gender: user?.gender,
            companyId: user.company?.id.toString() ?? "",
            roleId: user.role?.id.toString() ?? "",
          });

          setSelectedCompany(user.company ?? undefined);
          setSelectedRole(user.role ?? undefined);
        } catch (err) {
          // i18n.t rather than t: the effect keeps its dependencies, so switching language does not fetch again
          toast.error(errorMessage(err, i18n.t("upsert.notFound", { ns: "user" })));
          setSearchParams({});
        } finally {
          setIsLoading(false);
        }
      };

      fetchData();
    }
  }, [id, setSearchParams]);

  // ============================
  // Company selection handler
  // ============================
  const addCompany = (company: CompanyRef) => {
    const newCompany = selectedCompany?.id === company.id ? undefined : company;
    setSelectedCompany(newCompany);

    const companyId = newCompany?.id;

    setFormData((prev) => ({
      ...prev,
      companyId: companyId ? companyId.toString() : "",
    }));
  };

  const removeCompany = () => {
    setSelectedCompany(undefined);
    setFormData((prev) => ({ ...prev, companyId: "" }));
  };

  // ============================
  // Company selection handler
  // ============================
  const addRole = (role: RoleRef) => {
    const newRole = selectedRole?.id === role.id ? undefined : role;
    setSelectedRole(newRole);

    const roleId = newRole?.id;

    setFormData((prev) => ({
      ...prev,
      roleId: roleId ? roleId.toString() : "",
    }));
  };

  const removeRole = () => {
    setSelectedRole(undefined);
    setFormData((prev) => ({ ...prev, roleId: "" }));
  };

  // ============================
  // Form Submit Handler
  // ============================
  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const company = selectedCompany?.id ? selectedCompany : null;
      const role = selectedRole?.id ? selectedRole : null;

      if (isEdit) {
        await userUseCases.update({
          id: parseInt(id!),
          name: formData.name,
          gender: formData.gender,
          dob: new Date(formData.dob).toISOString(),
          address: formData.address,
          companyId: company?.id ?? null,
          roleId: role?.id ?? null,
        });
      } else {
        await userUseCases.create({
          name: formData.name,
          email: formData.email,
          password: formData.password,
          dob: new Date(formData.dob).toISOString(),
          address: formData.address,
          gender: formData.gender,
          companyId: company?.id ?? null,
          roleId: role?.id ?? null,
        });
      }

      toast.success(isEdit ? t("upsert.updated") : t("upsert.created"));
      handleBack();
    } catch (err) {
      toast.error(
        errorMessage(
          err,
          isEdit ? t("upsert.updateFailed") : t("upsert.createFailed"),
        ),
      );
    } finally {
      setIsLoading(false);
    }
  };

  // ============================
  // General Field Handler
  // ============================
  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: "" }));
    }
  };

  // ============================
  // Utils Handler
  // ============================
  const handleBack = () => {
    navigate("/admin/user-manager");
  };

  useEffect(() => {
    if (isEdit && !permissions.includes("PUT /users"))
      navigate("/admin/user-manager");
    else if (!isEdit && !permissions.includes("POST /users"))
      navigate("/admin/user-manager");
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
          <CardTitle>{t("upsert.personalInfo")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="space-y-6">
            {/* Row 1 - Basic Info */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="name">
                  {t("fields.fullName")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="name"
                  placeholder={t("upsert.namePlaceholder")}
                  value={formData.name}
                  onChange={(e) => handleInputChange("name", e.target.value)}
                  className={errors.name ? "border-red-500" : ""}
                  required
                />
                {errors.name && (
                  <span className="text-xs text-red-500">{errors.name}</span>
                )}
              </div>

              {/* Email (only for create mode) */}
              {!isEdit && (
                <div className="space-y-2">
                  <Label htmlFor="email">
                    {t("common:fields.email")} <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="email"
                    type="email"
                    placeholder="example@email.com"
                    value={formData.email}
                    onChange={(e) => handleInputChange("email", e.target.value)}
                    className={errors.email ? "border-red-500" : ""}
                    required
                  />
                  {errors.email && (
                    <span className="text-xs text-red-500">{errors.email}</span>
                  )}
                </div>
              )}
            </div>

            {/* Row 2 - Password and DOB */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              {/* Password (only for create mode) */}
              {!isEdit && (
                <div className="space-y-2">
                  <Label htmlFor="password">
                    {t("common:fields.password")} <span className="text-red-500">*</span>
                  </Label>
                  <div className="relative">
                    <Input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      placeholder={t("upsert.passwordPlaceholder")}
                      value={formData.password}
                      onChange={(e) =>
                        handleInputChange("password", e.target.value)
                      }
                      className={`pr-10 ${errors.password ? "border-red-500" : ""}`}
                      required
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="absolute top-0 right-0 h-full px-3 py-2 hover:bg-transparent"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      {showPassword ? (
                        <EyeOff className="h-4 w-4 text-gray-400" />
                      ) : (
                        <Eye className="h-4 w-4 text-gray-400" />
                      )}
                    </Button>
                  </div>
                  {errors.password && (
                    <span className="text-xs text-red-500">
                      {errors.password}
                    </span>
                  )}
                </div>
              )}

              <div className="space-y-2">
                <Label htmlFor="dob">
                  {t("fields.dob")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="dob"
                  type="date"
                  value={formData.dob}
                  onChange={(e) => handleInputChange("dob", e.target.value)}
                  className={`w-fit ${errors.dob ? "border-red-500" : ""}`}
                  max={new Date().toISOString().split("T")[0]}
                  required
                />
                {errors.dob && (
                  <span className="text-xs text-red-500">{errors.dob}</span>
                )}
              </div>
            </div>

            {/* Row 3 - Address and Gender */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="address">
                  {t("common:fields.address")} <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="address"
                  placeholder={t("upsert.addressPlaceholder")}
                  value={formData.address}
                  onChange={(e) => handleInputChange("address", e.target.value)}
                  className={errors.address ? "border-red-500" : ""}
                  required
                />
                {errors.address && (
                  <span className="text-xs text-red-500">{errors.address}</span>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="gender">
                  {t("fields.gender")} <span className="text-red-500">*</span>
                </Label>
                <Select
                  value={formData.gender}
                  onValueChange={(value: "MALE" | "FEMALE" | "OTHER") =>
                    handleInputChange("gender", value)
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder={t("fields.genderPlaceholder")} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="MALE">{t("gender.MALE")}</SelectItem>
                    <SelectItem value="FEMALE">{t("gender.FEMALE")}</SelectItem>
                    <SelectItem value="OTHER">{t("gender.OTHER")}</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Company and Role Section */}
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        {/* Company Selection */}
        <CompanySelection
          selectedCompany={selectedCompany}
          addCompany={addCompany}
          removeCompany={removeCompany}
        />

        {/* Role Selection */}
        <RoleSelection
          selectedRole={selectedRole}
          addRole={addRole}
          removeRole={removeRole}
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
          className="bg-blue-500 text-white hover:bg-blue-600"
        >
          {isLoading
            ? t("upsert.processing")
            : isEdit
              ? t("common:actions.update")
              : t("upsert.submitCreate")}
        </Button>
      </div>
    </div>
  );
}
