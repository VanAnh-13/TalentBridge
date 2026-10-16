import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/shared/presentation/ui/dialog.tsx";
import { DialogDescription } from "@radix-ui/react-dialog";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { ScrollArea } from "@/shared/presentation/ui/scroll-area.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/presentation/ui/select.tsx";
import { BetweenVerticalStart } from "lucide-react";
import { useTranslation } from "react-i18next";

import type { Resume, StatusChange } from "../../domain/resume";
import PDFViewer from "@/shared/presentation/components/PDFViewer.tsx";
import RichTextPreview from "@/shared/presentation/components/RichText/index-preview.tsx";
import { statusOptions } from "@/shared/presentation/tagColors.ts";

interface ViewResumeDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onUpdate: (resume: StatusChange) => Promise<void>;
  resume: Resume;
  onCloseForm: () => void;
  theme?: "blue" | "purple";
}

export function ViewResumeDialog({
  open,
  onOpenChange,
  onUpdate,
  resume,
  onCloseForm,
  theme = "blue",
}: ViewResumeDialogProps) {
  const { t } = useTranslation(["resume", "common"]);
  const [status, setStatus] = useState(resume.status);
  const [openJobInfo, setOpenJobInfo] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (open) setStatus(resume.status);
    else {
      setStatus("");
      setOpenJobInfo(false);
    }
  }, [open, resume.status]);

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();

    setIsLoading(true);
    const res: StatusChange = {
      id: resume.id,
      status,
    };
    await onUpdate(res);
    setIsLoading(false);

    onOpenChange(false);
    onCloseForm?.();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={`flex !h-11/12 !max-h-none !w-full !max-w-none flex-col ${openJobInfo ? "lg:!w-11/12" : "lg:!w-2/3"}`}
      >
        <DialogHeader>
          <DialogTitle className="text-center">{t("view.title")}</DialogTitle>
          <DialogDescription className="text-center">
            {t("view.description")}
          </DialogDescription>
        </DialogHeader>

        <Button
          className={`w-fit hover:-translate-y-0.5 ${
            openJobInfo
              ? `border border-gray-100 text-white ${theme === "blue" ? "bg-blue-500 hover:bg-blue-600" : "bg-purple-500 hover:bg-purple-600"}`
              : `border bg-white hover:bg-white ${theme === "blue" ? "border-blue-500 text-blue-500" : "border-purple-500 text-purple-500"}`
          }`}
          onClick={() => setOpenJobInfo((v) => !v)}
        >
          <BetweenVerticalStart />
          {openJobInfo ? t("view.hideJobInfo") : t("view.showJobInfo")}
        </Button>

        {!openJobInfo && (
          <ScrollArea className="h-3/4">
            <PDFViewer fileUrl={resume.pdfUrl} />
          </ScrollArea>
        )}

        {openJobInfo && (
          <div className="mt-2 min-h-0 flex-1">
            <div className="grid h-full w-full grid-cols-2 gap-12">
              {/* Column 1: PDF */}

              <ScrollArea className="h-full min-h-0 border px-2">
                <h1 className="my-4 text-center text-3xl font-bold">
                  {t("view.title")}
                </h1>
                <PDFViewer fileUrl={resume.pdfUrl} />
              </ScrollArea>

              {/* Column 2: job details */}
              <ScrollArea className="h-full min-h-0 border px-2">
                <h1 className="my-4 text-center text-3xl font-bold">
                  {t("job.info")}
                </h1>
                <div className="flex h-full flex-col space-y-5">
                  <div>
                    <span className="font-semibold text-gray-700">
                      {t("job.requiredSkills")}:
                    </span>
                    <div className="mt-2 flex flex-wrap gap-2">
                      {resume.job.skills.map((skill) => (
                        <Badge
                          key={skill}
                          className={`${theme === "blue" ? "bg-blue-100 text-blue-700" : "bg-purple-100 text-purple-700"}`}
                        >
                          {skill}
                        </Badge>
                      ))}
                    </div>
                  </div>
                  <div className="flex-1">
                    <span className="font-semibold text-gray-700">
                      {t("job.description")}:
                    </span>
                    <RichTextPreview content={resume.job.description} />
                  </div>
                </div>
              </ScrollArea>
            </div>
          </div>
        )}

        <div className="flex items-center justify-center gap-3">
          <Select value={status} onValueChange={setStatus}>
            <SelectTrigger className="w-40">
              <SelectValue placeholder={t("view.statusPlaceholder")} />
            </SelectTrigger>
            <SelectContent>
              {statusOptions.map((opt) => (
                <SelectItem value={opt.value} key={opt.value}>
                  {t(`common:resumeStatus.${opt.value}`)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Button
            onClick={handleUpdate}
            className={`cursor-pointer ${theme === "blue" ? "bg-blue-500 hover:bg-blue-600" : "bg-purple-500 hover:bg-purple-600"}`}
            disabled={isLoading}
          >
            {t("common:actions.update")}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
