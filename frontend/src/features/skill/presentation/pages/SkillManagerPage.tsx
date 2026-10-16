"use client";

import { useEffect, useState } from "react";

import { Button } from "@/shared/presentation/ui/button.tsx";
import { Plus } from "lucide-react";
import { toast } from "sonner";

import { skills as skillUseCases } from "@/app/container";
import type { NewSkill, Skill, SkillChange } from "../../domain/skill";
import Pagination from "@/shared/presentation/components/Pagination.tsx";
import { errorMessage } from "@/shared/domain/errors";
import { SkillForm } from "../components/SkillForm.tsx";
import { SkillSearchSection } from "../components/SkillSearchSection.tsx";
import { SkillTable } from "../components/SkillTable.tsx";
import { HasPermission } from "@/features/auth";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

interface SkillManagerProps {
  theme?: "blue" | "purple";
}

const SkillManagerPage = ({ theme = "blue" }: SkillManagerProps) => {
  const { t } = useTranslation("skill");

  // Data
  const [skills, setSkills] = useState<Skill[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // Search
  const [searchName, setSearchName] = useState("");

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Form State
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingSkill, setEditingSkill] =
    useState<Skill | null>(null);

  // ============================
  // HANDLE OPEN FORM
  // ============================
  const openEditForm = (skill: Skill) => {
    setEditingSkill(skill);
    setIsFormOpen(true);
  };

  const openCreateForm = () => {
    setEditingSkill(null);
    setIsFormOpen(true);
  };

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchSkills = async (
    page: number,
    size: number,
    searchName: string,
  ) => {
    setIsLoading(true);
    try {
      const res = await skillUseCases.search(searchName, page, size);

      setSkills(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      // i18n.t rather than t: fetchSkills stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("errors.loadList", { ns: "skill" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchSkills(currentPage, itemsPerPage, searchName);
  }, [currentPage, itemsPerPage, searchName]);

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setSearchName("");
    setCurrentPage(1);
    setIsFormOpen(false);
    setEditingSkill(null);

    fetchSkills(currentPage, itemsPerPage, searchName);
  };

  // ============================
  // HANDLE CREATE, UPDATE, DELETE
  // ============================
  const handleAddOrUpdateSkill = async (
    data: NewSkill | SkillChange,
  ) => {
    try {
      if (!data) return;

      if ("id" in data) {
        await skillUseCases.update(data);
        toast.success(t("toasts.updated"));
      } else {
        await skillUseCases.create(data);
        toast.success(t("toasts.created"));
      }

      handleReset();
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    }
  };

  const handleDeleteSkill = async (id: number) => {
    try {
      await skillUseCases.remove(id);
      toast.success(t("toasts.deleted"));
      handleReset();
    } catch (err) {
      toast.error(errorMessage(err, t("errors.deleteFailed")));
    }
  };

  return (
    <div className="space-y-6">
      <SkillSearchSection
        searchName={searchName}
        setSearchName={setSearchName}
        onReset={handleReset}
      />

      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">{t("manager.title")}</h2>
        <HasPermission perm={"POST /skills"}>
          <Button
            className={`${theme === "blue" ? "bg-blue-600 hover:bg-blue-700" : "bg-purple-600 hover:bg-purple-700"}`}
            onClick={openCreateForm}
          >
            <Plus className="mr-2 h-4 w-4" />
            {t("manager.add")}
          </Button>
        </HasPermission>
      </div>

      <SkillTable
        skills={skills}
        isLoading={isLoading}
        onEdit={openEditForm}
        onDelete={handleDeleteSkill}
        theme={theme}
      />

      <Pagination
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsPerPage={itemsPerPage}
        setItemsPerPage={setItemsPerPage}
        showItemsPerPageSelect={true}
        theme={theme}
      />

      <SkillForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSubmit={handleAddOrUpdateSkill}
        initialData={editingSkill}
        onCloseForm={() => setEditingSkill(null)}
        theme={theme}
      />
    </div>
  );
};

export default SkillManagerPage;
