import { UserMenu, useAuthState } from "@/features/auth";
import { NotificationBell } from "@/features/notification";
import { useLocation } from "react-router-dom";
import { Clock } from "lucide-react";
import { SidebarTrigger } from "@/shared/presentation/ui/sidebar";
import { LanguageSwitcher } from "@/shared/presentation/components/LanguageSwitcher";
import { currentLocale } from "@/shared/presentation/i18n/locale";
import { useTranslation } from "react-i18next";

type RouteKey = keyof typeof import("@/app/i18n/app").appMessages.en.admin.routes;

/** Title and subtitle of each page, as message keys under app:admin.routes. */
const routeTitles: Record<string, { key: RouteKey; icon?: string }> = {
  "/admin/dashboard": { key: "dashboard", icon: "📊" },
  "/admin/company": { key: "companies", icon: "🏢" },
  "/admin/user-manager": { key: "users", icon: "👥" },
  "/admin/recruitment/job-manager": { key: "jobs", icon: "💼" },
  "/admin/recruitment/job-manager/upsert": { key: "jobUpsert", icon: "➕" },
  "/admin/recruitment/skill-manager": { key: "skills", icon: "🛠️" },
  "/admin/resume": { key: "resumes", icon: "📄" },
  "/admin/access-control/permission": { key: "permissions", icon: "🔐" },
  "/admin/access-control/role": { key: "roles", icon: "🔐" },
};

export function AdminTopBar() {
  const { isLogin } = useAuthState();
  const location = useLocation();

  const { t } = useTranslation("app");
  const route = routeTitles[location.pathname];
  const routeKey: RouteKey = route?.key ?? "fallback";
  const currentRoute = {
    icon: route?.icon,
    title: t(`admin.routes.${routeKey}.title`),
    subtitle: t(`admin.routes.${routeKey}.subtitle`),
  };

  return (
    <header className="sticky top-0 z-50 flex h-16 items-center justify-between border-b border-gray-200 bg-white/95 px-6 shadow-sm backdrop-blur-md">
      {/* Left side */}
      <div className="flex items-center gap-4">
        <SidebarTrigger
          className={`transition-colors hover:bg-blue-50 hover:text-blue-600 lg:hidden`}
        />
        <div className="flex items-center gap-3">
          {currentRoute.icon && (
            <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-blue-200 bg-gradient-to-br from-blue-50 to-blue-100">
              <span className="text-lg">{currentRoute.icon}</span>
            </div>
          )}
          <div>
            <h1 className="bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-xl font-bold text-gray-900">
              {currentRoute.title}
            </h1>
            {currentRoute.subtitle && (
              <p className="text-sm font-medium text-gray-500">
                {currentRoute.subtitle}
              </p>
            )}
          </div>
        </div>
      </div>

      {/* Center */}
      <div className="flex w-1/3 justify-center">
        <div className="flex items-center gap-2 rounded-lg border bg-gray-50 px-3 py-2">
          <Clock className="h-4 w-4 text-gray-500" />
          <span className="text-sm font-medium text-gray-700">
            {new Date().toLocaleTimeString(currentLocale(), {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </span>
        </div>
      </div>

      {/* Right side */}
      <div className="flex items-center gap-3">
        <LanguageSwitcher />
        {isLogin && (
          <>
          <NotificationBell />
          <UserMenu blackTheme />
          </>
        )}
      </div>
    </header>
  );
}
