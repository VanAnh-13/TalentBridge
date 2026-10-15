import { toneOf, TONE_LABEL } from "../../domain/match";
import type { ScoreTone } from "../../domain/match";

const CLASSES: Record<ScoreTone, string> = {
  excellent: "bg-emerald-100 text-emerald-800 border-emerald-300",
  good: "bg-green-100 text-green-800 border-green-300",
  partial: "bg-amber-100 text-amber-800 border-amber-300",
  poor: "bg-red-100 text-red-800 border-red-300",
};

/** The AI score of an application, coloured by band. */
const ScoreBadge = ({ score }: { score: number }) => {
  const tone = toneOf(score);
  return (
    <span
      title={TONE_LABEL[tone]}
      className={`inline-flex min-w-14 items-center justify-center rounded-full border px-2.5 py-0.5 text-sm font-semibold ${CLASSES[tone]}`}
    >
      {score}
    </span>
  );
};

export default ScoreBadge;
