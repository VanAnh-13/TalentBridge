import { Plus, X } from "lucide-react";
import { useTranslation } from "react-i18next";

import { Button } from "@/shared/presentation/ui/button";
import { Input } from "@/shared/presentation/ui/input";
import { Label } from "@/shared/presentation/ui/label";

import { MAX_DURATION_MINUTES, MAX_SLOTS, MIN_DURATION_MINUTES } from "../../domain/interview";
import type { InterviewMode } from "../../domain/interview";
import type { OfferDraft } from "./offerDraft";

interface Props {
  draft: OfferDraft;
  onChange: (draft: OfferDraft) => void;
}

const InterviewOfferForm = ({ draft, onChange }: Props) => {
  const { t } = useTranslation("interview");
  const set = <K extends keyof OfferDraft>(key: K, value: OfferDraft[K]) => onChange({ ...draft, [key]: value });
  const setSlot = (index: number, value: string) =>
    set("slots", draft.slots.map((slot, i) => (i === index ? value : slot)));

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 gap-3">
        <div className="space-y-1">
          <Label htmlFor="iv-mode">{t("form.mode")}</Label>
          <select
            id="iv-mode"
            value={draft.mode}
            onChange={(e) => set("mode", e.target.value as InterviewMode)}
            className="h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm"
          >
            <option value="ONLINE">{t("mode.ONLINE")}</option>
            <option value="ONSITE">{t("mode.ONSITE")}</option>
          </select>
        </div>
        <div className="space-y-1">
          <Label htmlFor="iv-duration">{t("form.duration")}</Label>
          <Input
            id="iv-duration"
            type="number"
            min={MIN_DURATION_MINUTES}
            max={MAX_DURATION_MINUTES}
            value={draft.durationMinutes}
            onChange={(e) => set("durationMinutes", Number(e.target.value))}
          />
        </div>
      </div>

      <div className="space-y-1">
        <Label htmlFor="iv-location">{draft.mode === "ONLINE" ? t("form.link") : t("form.place")}</Label>
        <Input
          id="iv-location"
          value={draft.location}
          maxLength={500}
          placeholder={draft.mode === "ONLINE" ? "https://meet.google.com/..." : t("form.addressPlaceholder")}
          onChange={(e) => set("location", e.target.value)}
        />
      </div>

      <div className="space-y-1">
        <Label htmlFor="iv-note">{t("form.note")}</Label>
        <Input
          id="iv-note"
          value={draft.note}
          maxLength={1000}
          placeholder={t("form.notePlaceholder")}
          onChange={(e) => set("note", e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label>{t("form.slots", { max: MAX_SLOTS })}</Label>
        {draft.slots.map((slot, index) => (
          <div key={index} className="flex items-center gap-2">
            <Input type="datetime-local" value={slot} onChange={(e) => setSlot(index, e.target.value)} />
            {draft.slots.length > 1 && (
              <Button
                type="button"
                variant="ghost"
                size="icon"
                aria-label={t("form.removeSlot")}
                onClick={() => set("slots", draft.slots.filter((_, i) => i !== index))}
              >
                <X className="h-4 w-4" />
              </Button>
            )}
          </div>
        ))}
        {draft.slots.length < MAX_SLOTS && (
          <Button type="button" variant="outline" size="sm" onClick={() => set("slots", [...draft.slots, ""])}>
            <Plus className="mr-1 h-4 w-4" /> {t("form.addSlot")}
          </Button>
        )}
      </div>
    </div>
  );
};

export default InterviewOfferForm;
