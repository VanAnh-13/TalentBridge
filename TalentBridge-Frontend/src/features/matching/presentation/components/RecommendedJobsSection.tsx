import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { matching as matchingUseCases } from "@/app/container";
import { useAuthState } from "@/features/auth";

import type { Recommendation } from "../../domain/match";

/** "Recommended for you" on the home page: shown only to signed-in candidates who follow some skills. */
const RecommendedJobsSection = () => {
  const { isLogin, user } = useAuthState();
  const allowed = isLogin && user?.permissions?.includes("GET /matching/me/recommendations");
  const [items, setItems] = useState<Recommendation[]>([]);

  useEffect(() => {
    if (!allowed) return;
    matchingUseCases.recommendations(6).then(setItems).catch(() => setItems([]));
  }, [allowed]);

  if (!allowed || items.length === 0) return null;

  return (
    <section className="mx-auto max-w-6xl px-4 py-8">
      <h2 className="mb-1 text-2xl font-bold">Gợi ý cho bạn</h2>
      <p className="text-muted-foreground mb-4 text-sm">Dựa trên các kỹ năng bạn đang theo dõi.</p>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {items.map((item) => (
          <Link
            key={item.jobId}
            to={`/jobs/${item.jobId}`}
            className="rounded-lg border bg-white p-4 shadow-sm transition hover:shadow-md"
          >
            <div className="flex items-start justify-between gap-2">
              <h3 className="font-semibold">{item.name}</h3>
              <span className="rounded-full bg-blue-100 px-2 py-0.5 text-xs font-semibold text-blue-800" title="Tỉ lệ kỹ năng yêu cầu bạn đã có">
                {item.fitPercent}%
              </span>
            </div>
            <p className="text-muted-foreground text-sm">
              {[item.companyName, item.location].filter(Boolean).join(" · ")}
            </p>
            <div className="mt-2 flex flex-wrap gap-1">
              {item.skills.map((skill) => (
                <span
                  key={skill}
                  className={`rounded px-1.5 py-0.5 text-xs ${item.sharedSkills.includes(skill) ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-600"}`}
                >
                  {skill}
                </span>
              ))}
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
};

export default RecommendedJobsSection;
