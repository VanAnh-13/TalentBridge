import { RefreshCw, Sparkles } from "lucide-react";
import i18n from "i18next";
import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "sonner";

import { jobs as jobUseCases, matching as matchingUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import { formatISO } from "@/shared/presentation/format";
import { Button } from "@/shared/presentation/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/presentation/ui/card";

import { toneOf } from "../../domain/match";
import type { MatchingStatus, RankedCandidate } from "../../domain/match";
import ScoreBadge from "../components/ScoreBadge";

interface JobOption {
  id: number;
  name: string;
}

const Skills = ({ names, className }: { names: string[]; className: string }) =>
  names.length === 0 ? null : (
    <div className="flex flex-wrap gap-1">
      {names.map((name) => (
        <span key={name} className={`rounded px-1.5 py-0.5 text-xs ${className}`}>
          {name}
        </span>
      ))}
    </div>
  );

/** The applicants of one job, best AI match first, with the reasons behind each score. */
const CandidateRankingPage = () => {
  const { t } = useTranslation(["matching", "common"]);
  const [options, setOptions] = useState<JobOption[]>([]);
  const [jobId, setJobId] = useState<number | null>(null);
  const [status, setStatus] = useState<MatchingStatus | null>(null);
  const [ranked, setRanked] = useState<RankedCandidate[]>([]);
  const [loading, setLoading] = useState(false);
  const [busy, setBusy] = useState<number | null>(null);

  useEffect(() => {
    void (async () => {
      try {
        const [found, state] = await Promise.all([
          jobUseCases.search("company", {}, 1, 100),
          matchingUseCases.status(),
        ]);
        const list = found.content.map((job) => ({ id: job.id, name: job.name }));
        setOptions(list);
        setStatus(state);
        if (list.length > 0) setJobId(list[0].id);
      } catch (err) {
        // i18n.t rather than t here and in load: switching language must not fetch again
        toast.error(errorMessage(err, i18n.t("ranking.jobsLoadFailed", { ns: "matching" })));
      }
    })();
  }, []);

  const load = useCallback(async () => {
    if (jobId === null) return;
    setLoading(true);
    try {
      setRanked(await matchingUseCases.candidatesOf(jobId));
    } catch (err) {
      toast.error(errorMessage(err, i18n.t("ranking.candidatesLoadFailed", { ns: "matching" })));
    } finally {
      setLoading(false);
    }
  }, [jobId]);

  useEffect(() => {
    void load();
  }, [load]);

  const recompute = async (resumeId: number) => {
    setBusy(resumeId);
    try {
      await matchingUseCases.recompute(resumeId);
      toast.success(t("ranking.recomputeQueued"));
    } catch (err) {
      toast.error(errorMessage(err, t("ranking.recomputeFailed")));
    } finally {
      setBusy(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h2 className="flex items-center gap-2 text-lg font-semibold">
          <Sparkles className="h-5 w-5 text-purple-600" />
          {t("ranking.title")}
        </h2>
        <div className="flex items-center gap-2">
          <select
            aria-label={t("ranking.selectJob")}
            className="rounded-md border px-3 py-2 text-sm"
            value={jobId ?? ""}
            onChange={(event) => setJobId(Number(event.target.value))}
          >
            {options.map((job) => (
              <option key={job.id} value={job.id}>
                {job.name}
              </option>
            ))}
          </select>
          <Button variant="outline" size="sm" onClick={() => void load()}>
            <RefreshCw className="mr-1 h-4 w-4" />
            {t("common:actions.refresh")}
          </Button>
        </div>
      </div>

      {status && !status.enabled && (
        <p className="rounded-md border border-amber-300 bg-amber-50 p-3 text-sm text-amber-800">
          {t("ranking.disabled")}
        </p>
      )}
      {status?.enabled && (
        <p className="text-muted-foreground text-xs">
          {t("ranking.enabled", { model: status.model ?? "" })}
        </p>
      )}

      {loading ? (
        <LoadingSpinner />
      ) : options.length === 0 ? (
        <EmptyState title={t("ranking.noJobs")} description={t("ranking.noJobsHint")} icon={<Sparkles className="text-muted-foreground mb-4 h-12 w-12" />} />
      ) : ranked.length === 0 ? (
        <EmptyState title={t("ranking.noCandidates")} description={t("ranking.noCandidatesHint")} icon={<Sparkles className="text-muted-foreground mb-4 h-12 w-12" />} />
      ) : (
        <div className="space-y-3">
          {ranked.map((candidate, index) => (
            <Card key={candidate.resumeId}>
              <CardHeader className="flex flex-row items-center justify-between gap-3 pb-2">
                <CardTitle className="flex items-center gap-3 text-base">
                  <span className="text-muted-foreground w-6 text-right text-sm">#{index + 1}</span>
                  {candidate.email}
                </CardTitle>
                <div className="flex items-center gap-3">
                  {candidate.match ? (
                    <>
                      <span className="text-muted-foreground text-xs">{t(`tone.${toneOf(candidate.match.score)}`)}</span>
                      <ScoreBadge score={candidate.match.score} />
                    </>
                  ) : (
                    <span className="text-muted-foreground text-sm">{t("ranking.notScored")}</span>
                  )}
                  {status?.enabled && (
                    <Button
                      variant="ghost"
                      size="sm"
                      disabled={busy === candidate.resumeId}
                      onClick={() => void recompute(candidate.resumeId)}
                    >
                      {candidate.match ? t("ranking.rescore") : t("ranking.score")}
                    </Button>
                  )}
                </div>
              </CardHeader>
              {candidate.match && (
                <CardContent className="space-y-2 text-sm">
                  <p>{candidate.match.summary}</p>
                  <Skills names={candidate.match.matchedSkills} className="bg-green-100 text-green-800" />
                  <Skills names={candidate.match.missingSkills} className="bg-red-100 text-red-800" />
                  <p className="text-muted-foreground text-xs">
                    {t("ranking.scoredAt", { date: formatISO(candidate.match.scoredAt) })}
                  </p>
                </CardContent>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default CandidateRankingPage;
