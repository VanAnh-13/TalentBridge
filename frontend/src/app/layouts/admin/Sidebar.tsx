import { useAccount } from "@/features/auth";
import { useCallback, useEffect, useState } from "react";
import {
  Building2,
  LayoutDashboard,
  FileText,
  Wrench,
  MonitorIcon as MonitorCog,
  User,
  ChevronRight,
  Briefcase,
  Settings,
  type LucideIcon,
  ShieldCheck,
  KeyRound,
  UserCog,
  Home,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/shared/presentation/ui/sidebar";
import { Link, useLocation } from "react-router-dom";
import { cn } from "@/shared/presentation/lib/utils";
import { useTranslation } from "react-i18next";
import type { ParseKeys } from "i18next";

type MenuKey = ParseKeys<"app">;

interface MenuItem {
  title: MenuKey;
  url?: string;
  icon: LucideIcon;
  permission?: string;
  children?: {
    title: MenuKey;
    url: string;
    icon?: LucideIcon;
    permission?: string;
  }[];
}

const menuItems: MenuItem[] = [
  {
    title: "admin.menu.dashboard",
    url: "/admin/dashboard",
    icon: LayoutDashboard,
  },
  {
    title: "admin.menu.companies",
    url: "/admin/company",
    icon: Building2,
    permission: "GET /companies",
  },
  {
    title: "admin.menu.recruitment",
    icon: Wrench,
    children: [
      {
        title: "admin.menu.jobs",
        url: "/admin/recruitment/job-manager",
        icon: Briefcase,
        permission: "GET /jobs",
      },
      {
        title: "admin.menu.skills",
        url: "/admin/recruitment/skill-manager",
        icon: Settings,
        permission: "GET /skills",
      },
      {
        title: "admin.menu.resumes",
        url: "/admin/resume",
        icon: FileText,
        permission: "GET /resumes",
      },
    ],
  },
  {
    title: "admin.menu.users",
    url: "/admin/user-manager",
    icon: User,
    permission: "GET /users",
  },
  {
    title: "admin.menu.accessControl",
    icon: ShieldCheck,
    children: [
      {
        title: "admin.menu.permissions",
        url: "/admin/access-control/permission",
        icon: KeyRound,
        permission: "GET /permissions/*",
      },
      {
        title: "admin.menu.roles",
        url: "/admin/access-control/role",
        icon: UserCog,
        permission: "GET /roles",
      },
    ],
  },
];

export function AdminSidebar() {
  const { t } = useTranslation("app");
  const { pathname } = useLocation();
  const permissions =
    useAccount()?.permissions ?? [];

  // ======================================================================================================
  // Which parent menus are expanded (keyed by their message key, so it survives a language switch)
  // ======================================================================================================
  const [expandedItems, setExpandedItems] = useState<string[]>([]);

  /**
   * Expands or collapses a parent menu.
   *
   * For example:
   *   + open "recruitment":  toggleExpanded("admin.menu.recruitment")    => ["admin.menu.recruitment"]
   *   + open "access":       toggleExpanded("admin.menu.accessControl")  => [..., "admin.menu.accessControl"]
   *   + close "recruitment": toggleExpanded("admin.menu.recruitment")    => ["admin.menu.accessControl"]
   */
  const toggleExpanded = (title: string) => {
    setExpandedItems((prev) =>
      prev.includes(title)
        ? prev.filter((item) => item !== title)
        : [...prev, title],
    );
  };

  // ======================================================================================================
  // Which menu entries are active
  // ======================================================================================================
  /**
   * Whether a top-level entry is active.
   * - Without children: compared with the current path.
   * - With children: active as soon as one child is.
   */
  const isItemActive = (item: MenuItem): boolean => {
    if (item.url) {
      // no children
      return pathname === item.url || pathname.startsWith(item.url + "/");
    }
    if (item.children) {
      // with children: active when one of them is
      return item.children.some(
        (child) =>
          pathname === child.url || pathname.startsWith(child.url + "/"),
      );
    }
    return false;
  };

  /**
   * Whether a second-level entry is active.
   */
  const isChildActive = useCallback(
    (childUrl: string): boolean =>
      pathname === childUrl || pathname.startsWith(childUrl + "/"),
    [pathname],
  );

  // ======================================================================================================
  // Opening a child's route directly expands its parent, so the sidebar always matches the current URL.
  //======================================================================================================
  useEffect(() => {
    menuItems.forEach((item) => {
      if (
        item.children &&
        item.children.some((child) => isChildActive(child.url))
      ) {
        setExpandedItems((prev) =>
          prev.includes(item.title) ? prev : [...prev, item.title],
        );
      }
    });
  }, [pathname, isChildActive]);

  return (
    <Sidebar className="border-r border-blue-100 bg-gradient-to-b from-blue-50/30 to-white">
      {/*HEADER*/}
      <div className="flex h-16 items-center border-b border-blue-100 bg-gradient-to-r from-blue-600 to-blue-700 px-6">
        <div className="flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded border border-white/30 bg-white/20">
            <MonitorCog className="h-4 w-4 text-white" />
          </div>
          <div>
            <span className="text-lg font-semibold text-white">{t("admin.brand")}</span>
            <p className="text-xs text-blue-100">{t("admin.brandSubtitle")}</p>
          </div>
        </div>
      </div>

      <SidebarContent className="px-3 py-4">
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu className="space-y-1">
              {menuItems
                .filter(
                  (item) =>
                    !item.permission || permissions.includes(item.permission),
                )
                .map((item) => {
                  const isActive = isItemActive(item);
                  const isExpanded = expandedItems.includes(item.title);
                  const hasChildren = item.children && item.children.length > 0;
                  const filteredChildren = item.children?.filter(
                    (child) =>
                      !child.permission ||
                      permissions.includes(child.permission),
                  );

                  return (
                    <SidebarMenuItem key={item.title}>
                      {/* top-level entry with children */}
                      {hasChildren && (
                        <>
                          {/* top level */}
                          <div
                            onClick={() => toggleExpanded(item.title)}
                            className={cn(
                              "flex w-full cursor-pointer items-center justify-between rounded-md px-3 py-2 text-sm font-medium",
                              isActive
                                ? "bg-gradient-to-r from-blue-500 to-blue-600 text-white"
                                : "text-gray-700 hover:bg-blue-50 hover:text-blue-700",
                            )}
                          >
                            <div className="flex items-center gap-3">
                              <item.icon className="h-4 w-4" />
                              <span>{t(item.title)}</span>
                            </div>
                            <ChevronRight
                              className={cn(
                                "h-4 w-4",
                                isExpanded ? "rotate-90" : "rotate-0",
                              )}
                            />
                          </div>
                          {/* second level */}
                          {isExpanded && (
                            <div className="mt-1 ml-6 space-y-1 border-l border-gray-200 pl-3">
                              {filteredChildren?.map((child) => (
                                <SidebarMenuButton
                                  asChild
                                  key={child.title}
                                  isActive={isChildActive(child.url)}
                                  className={cn(
                                    "w-full justify-start rounded-md py-2 text-sm",
                                    isChildActive(child.url)
                                      ? "bg-blue-100 text-blue-800"
                                      : "text-gray-600 hover:bg-blue-50 hover:text-blue-700",
                                  )}
                                >
                                  <Link
                                    to={child.url}
                                    className="flex w-full items-center gap-3"
                                  >
                                    {child.icon ? (
                                      <child.icon className="h-4 w-4" />
                                    ) : (
                                      <div className="h-1.5 w-1.5 rounded-full bg-current"></div>
                                    )}
                                    <span>{t(child.title)}</span>
                                  </Link>
                                </SidebarMenuButton>
                              ))}
                            </div>
                          )}
                        </>
                      )}
                      {/* top-level entry without children */}
                      {!hasChildren && (
                        <>
                          <SidebarMenuButton
                            asChild
                            isActive={isActive}
                            className={cn(
                              "w-full justify-start rounded-md py-2 text-sm font-medium",
                              isActive
                                ? "bg-gradient-to-r from-blue-500 to-blue-600"
                                : "text-gray-700 hover:bg-blue-50 hover:text-blue-700",
                            )}
                          >
                            <Link
                              to={item.url!}
                              className="flex w-full items-center gap-3"
                            >
                              <item.icon
                                className={`h-4 w-4 ${isActive ? "text-white" : ""}`}
                              />
                              <span
                                className={`${isActive ? "font-bold text-white" : ""}`}
                              >
                                {t(item.title)}
                              </span>
                            </Link>
                          </SidebarMenuButton>
                        </>
                      )}
                    </SidebarMenuItem>
                  );
                })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="border-t border-blue-100 px-3 py-4">
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton
              asChild
              className="w-full justify-start rounded-md py-2 text-sm font-medium text-gray-700 hover:bg-blue-50 hover:text-blue-700"
            >
              <Link to="/" className="flex w-full items-center gap-3">
                <Home className="h-4 w-4" />
                <span>{t("nav.backHome")}</span>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}
