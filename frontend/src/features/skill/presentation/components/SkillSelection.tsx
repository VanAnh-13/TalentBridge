import { useEffect, useState } from "react";
import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/shared/presentation/ui/dialog.tsx";
import { Input } from "@/shared/presentation/ui/input.tsx";
import { Label } from "@/shared/presentation/ui/label.tsx";
import { Checkbox } from "@/shared/presentation/ui/checkbox.tsx";
import { Search, Wrench, X } from "lucide-react";
import { toast } from "sonner";
import { skills as skillUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import type { SkillRef } from "../../domain/skill";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner.tsx";
import { EmptyState } from "@/shared/presentation/components/EmptyState.tsx";
import Pagination from "@/shared/presentation/components/Pagination.tsx";
import { DialogDescription } from "@radix-ui/react-dialog";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

interface SkillSelectionProps {
  selectedSkills: SkillRef[];
  onAddSkill: (skill: SkillRef) => void;
  onRemoveSkill: (skill: SkillRef) => void;
}

const SkillSelection = ({
  selectedSkills,
  onAddSkill,
  onRemoveSkill,
}: SkillSelectionProps) => {
  const { t } = useTranslation(["skill", "common"]);

  // ============================
  // Modal State
  // ============================
  const [showDialog, setShowDialog] = useState(false);
  const closeModal = () => setShowDialog(false);

  // ============================
  // Pagination State
  // ============================
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(15);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // ============================
  // Fetching State
  // ============================
  const [allSkills, setAllSkills] = useState<SkillRef[]>([]);
  const [searchName, setSearch] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const fetchSkills = async (
    searchName: string,
    currentPage: number,
    itemsPerPage: number,
  ) => {
    try {
      setIsLoading(true);
      const data = await skillUseCases.search(searchName, currentPage, itemsPerPage);

      const mapped: SkillRef[] = data.content.map((item) => ({
        id: item.id,
        name: item.name,
      }));

      setAllSkills(mapped);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
      setIsLoading(false);
    } catch (err) {
      // i18n.t rather than t: fetchSkills stays free of render values, so the effects calling it need no extra dependency
      toast.error(errorMessage(err, i18n.t("errors.loadList", { ns: "skill" })));
    }
  };

  useEffect(() => {
    fetchSkills(searchName, currentPage, itemsPerPage);
  }, [searchName, currentPage, itemsPerPage]);

  // ============================
  // Handle Add/Remove Skill
  // ============================
  const handleToggleSkill = (skill: SkillRef) => {
    const exists = selectedSkills.some((s) => s.id === skill.id);
    if (exists) {
      onRemoveSkill(skill);
    } else {
      onAddSkill(skill);
    }
  };

  return (
    <div className="space-y-2">
      <Label>
        {t("selection.label")} <span className="text-red-500">*</span>
      </Label>

      <div className="rounded-md border bg-gray-50/50 p-3">
        {selectedSkills && selectedSkills.length > 0 && (
          <div className="mb-3 min-h-[100px]">
            <div className="flex flex-wrap gap-2">
              {selectedSkills.map((skill) => (
                <Badge key={skill.id} className="flex items-center gap-1">
                  {skill.name}
                  <X
                    className="h-3 w-3 cursor-pointer hover:text-red-500"
                    onClick={() => onRemoveSkill(skill)}
                  />
                </Badge>
              ))}
            </div>
          </div>
        )}

        {selectedSkills.length === 0 && (
          <div className="mb-3 flex min-h-[100px] w-full items-center justify-center rounded-lg border-2 border-dashed border-gray-300">
            <div>
              <Wrench className="mx-auto mb-2 h-8 w-8 text-gray-400" />
              <p className="text-sm text-gray-500">{t("selection.none")}</p>
            </div>
          </div>
        )}

        <Dialog open={showDialog} onOpenChange={setShowDialog}>
          <DialogTrigger asChild>
            <Button variant="outline" size="sm" className="w-full">
              <Wrench className="mr-2 h-4 w-4" />
              {selectedSkills.length > 0 ? t("selection.edit") : t("selection.choose")}
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>{t("selection.title")}</DialogTitle>
              <DialogDescription>{t("selection.description")}</DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="relative">
                <Search className="text-muted-foreground absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2" />
                <Input
                  placeholder={t("selection.searchPlaceholder")}
                  value={searchName}
                  onChange={(e) => setSearch(e.target.value)}
                  className="pl-10"
                />
              </div>

              <div className="max-h-60 min-h-[300px] space-y-2 overflow-y-auto">
                {isLoading && (
                  <div className="flex justify-center py-6">
                    <LoadingSpinner />
                  </div>
                )}

                {!isLoading && allSkills.length === 0 && (
                  <EmptyState
                    title={t("notFound.title")}
                    description={t("notFound.description")}
                    icon={
                      <Wrench className="text-muted-foreground mb-4 h-12 w-12" />
                    }
                  />
                )}

                {!isLoading &&
                  allSkills.length > 0 &&
                  allSkills.map((skill) => (
                    <div key={skill.id} className="flex items-center space-x-2">
                      <Checkbox
                        id={`skill-${skill.id}`}
                        checked={selectedSkills.some((s) => s.id === skill.id)}
                        onCheckedChange={() => handleToggleSkill(skill)}
                      />
                      <Label
                        htmlFor={`skill-${skill.id}`}
                        className="flex-1 cursor-pointer"
                      >
                        {skill.name}
                      </Label>
                    </div>
                  ))}
              </div>

              {!isLoading && totalPages > 1 && (
                <Pagination
                  currentPage={currentPage}
                  setCurrentPage={setCurrentPage}
                  totalPages={totalPages}
                  totalElements={totalElements}
                  itemsPerPage={itemsPerPage}
                  setItemsPerPage={setItemsPerPage}
                  showItemsPerPageSelect={false}
                />
              )}

              <div className="flex justify-end gap-2">
                <Button onClick={closeModal}>{t("common:actions.confirm")}</Button>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
};

export default SkillSelection;
