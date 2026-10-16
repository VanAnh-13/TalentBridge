import { useAuthState } from "@/features/auth";
import { User, BarChart3, Settings, Mail, CalendarCheck, MessageCircle } from "lucide-react";
import { Navigate, NavLink, Outlet } from "react-router-dom";
import { Trans, useTranslation } from "react-i18next";
import type { ParseKeys } from "i18next";

/** Entries of the candidate's own area; an entry with a permission shows only to people who hold it. */
const navigationItems: { title: ParseKeys<"app">; icon: typeof User; href: string; permission?: string }[] = [
  { title: "user.menu.info", icon: User, href: "info" },
  { title: "user.menu.subscriber", icon: Mail, href: "subscriber", permission: "GET /subscribers/me" },
  { title: "user.menu.resumes", icon: BarChart3, href: "resumes", permission: "POST /resumes" },
  { title: "user.menu.chat", icon: MessageCircle, href: "chat", permission: "GET /chats" },
  { title: "user.menu.interviews", icon: CalendarCheck, href: "interviews", permission: "GET /interviews/me" },
  { title: "user.menu.sessions", icon: Settings, href: "sessions" },
];

export default function UserPage() {
  const { t } = useTranslation("app");
  const { isLogin, user } = useAuthState();

  if (!isLogin || !user) return <Navigate to="/auth?mode=login" replace />;

  return (
    <>
      <div className="flex min-h-screen bg-gray-100 p-4">
        {/* Sidebar */}
        <div
          className={`hidden w-64 transform rounded-lg bg-white shadow-lg lg:block`}
          style={{ height: "fit-content", maxHeight: "80vh" }}
        >
          {/* Header */}
          <div className="flex items-center justify-between border-b border-gray-200 p-6">
            <h1 className="text-xl font-semibold text-gray-800">
              <Trans
                t={t}
                i18nKey="user.greeting"
                values={{ name: user.name }}
                components={{ highlight: <span className="text-yellow-500" /> }}
              />
            </h1>
          </div>

          {/* Navigation */}
          <nav className="p-4">
            <ul className="space-y-2">
              {navigationItems
                .filter((item) => !item.permission || user.permissions?.includes(item.permission))
                .map((item) => (
                  <li key={item.title}>
                    <NavLink
                      to={item.href}
                      className={({ isActive }) =>
                        `flex items-center rounded-lg px-3 py-2 text-sm font-medium transition-colors duration-200 ${
                          isActive
                            ? "bg-blue-100 text-blue-700"
                            : "text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                        } `
                      }
                    >
                      <item.icon className="mr-3 h-5 w-5" />
                      {t(item.title)}
                    </NavLink>
                  </li>
                ))}
            </ul>
          </nav>
        </div>

        {/* Main Content */}
        <div className="flex-1 lg:ml-6">
          <Outlet />
        </div>
      </div>
    </>
  );
}
