import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "sonner";

import { interviews as interviewUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { Button } from "@/shared/presentation/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/shared/presentation/ui/dialog";

import { InvalidOfferError } from "../../application/interviewUseCases";
import { MAX_DURATION_MINUTES, MAX_SLOTS, MIN_DURATION_MINUTES } from "../../domain/interview";
import type { Interview, OfferProblem } from "../../domain/interview";
import InterviewOfferForm from "./InterviewOfferForm";
import { emptyDraft, toInstant } from "./offerDraft";
import type { OfferDraft } from "./offerDraft";

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  /** Invite the candidate of this application... */
  resumeId?: number;
  /** ...or move this interview. */
  interview?: Interview;
  title?: string;
  onDone?: () => void;
}

/** A recruiter proposes time slots for an application, or proposes new ones for an existing interview. */
const ScheduleInterviewDialog = ({ open, onOpenChange, resumeId, interview, title, onDone }: Props) => {
  const { t } = useTranslation(["interview", "common"]);
  const [draft, setDraft] = useState<OfferDraft>(emptyDraft());
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!open) return;
    setDraft(
      interview
        ? {
            mode: interview.mode,
            location: interview.location,
            note: interview.note ?? "",
            durationMinutes: interview.durationMinutes,
            slots: [""],
          }
        : emptyDraft(),
    );
    // the dialog is reset every time it opens, not when the interview object is refreshed
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const problemText = (problem: OfferProblem) =>
    problem === "badDuration"
      ? t("problems.badDuration", { min: MIN_DURATION_MINUTES, max: MAX_DURATION_MINUTES })
      : t(`problems.${problem}`, { max: MAX_SLOTS });

  const submit = async () => {
    const offer = { ...draft, slots: draft.slots.map(toInstant).filter(Boolean) };
    setSaving(true);
    try {
      if (interview) await interviewUseCases.reschedule(interview.id, offer);
      else if (resumeId !== undefined) await interviewUseCases.schedule(resumeId, offer);
      toast.success(interview ? t("schedule.rescheduled") : t("schedule.invited"));
      onOpenChange(false);
      onDone?.();
    } catch (err) {
      toast.error(
        err instanceof InvalidOfferError
          ? err.problems.map(problemText).join(". ")
          : errorMessage(err, t("schedule.failed")),
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{title ?? (interview ? t("schedule.reschedule") : t("schedule.invite"))}</DialogTitle>
          <DialogDescription>{t("schedule.description")}</DialogDescription>
        </DialogHeader>
        <InterviewOfferForm draft={draft} onChange={setDraft} />
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={saving}>
            {t("common:actions.cancel")}
          </Button>
          <Button onClick={submit} disabled={saving}>
            {saving ? t("schedule.sending") : t("schedule.send")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ScheduleInterviewDialog;
