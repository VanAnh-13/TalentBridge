import { Badge } from "@/shared/presentation/ui/badge.tsx";
import { ScrollArea } from "@/shared/presentation/ui/scroll-area.tsx";
import type { Job } from "@/features/job/domain/job";
import { formatISO, formatSalary } from "@/shared/presentation/format.ts";
import { useNavigate } from "react-router-dom";
import { Trans, useTranslation } from "react-i18next";

type JobsSection = {
  jobs: Job[];
};

const JobsSection = ({ jobs }: JobsSection) => {
  const navigate = useNavigate();
  const { t } = useTranslation(["company", "common"]);

  return (
    <>
      {jobs.length > 0 && (
        <div className="my-[200px] md:col-span-1">
          <h2 className="mb-4 text-xl font-bold text-gray-800">
            <Trans
              t={t}
              i18nKey="jobs.hiring"
              count={jobs.length}
              components={{ highlight: <span className="text-orange-500" /> }}
            />
          </h2>
          <ScrollArea className="h-[800px]">
            <div className="space-y-6">
              {jobs.map((job) => (
                <div
                  key={job.id}
                  className="cursor-pointer space-y-3 rounded-xl border bg-white p-6 shadow-sm"
                  onClick={() => navigate(`/jobs/${job.id}`)}
                >
                  <h3 className="text-lg font-bold text-orange-700">
                    {job.name}
                  </h3>
                  <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                    <p>
                      <strong>{t("jobs.location")}</strong> {job.location}
                    </p>
                    <p>
                      <strong>{t("jobs.salary")}</strong> {formatSalary(job.salary)}
                    </p>
                    <p>
                      <strong>{t("jobs.quantity")}</strong> {job.quantity}
                    </p>
                    <p>
                      <strong>{t("jobs.level")}</strong> {t(`common:levels.${job.level}`)}
                    </p>
                    <p>
                      <strong>{t("jobs.period")}</strong> {formatISO(job.startDate)} →{" "}
                      {formatISO(job.endDate)}
                    </p>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {job.skills.map((skill) => (
                      <Badge
                        key={skill.id}
                        className="rounded-full bg-orange-100 text-orange-700"
                      >
                        {skill.name}
                      </Badge>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </ScrollArea>
        </div>
      )}
    </>
  );
};

export default JobsSection;
