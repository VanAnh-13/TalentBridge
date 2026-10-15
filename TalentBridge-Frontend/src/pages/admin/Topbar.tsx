import { UserMenu, useAuthState } from "@/features/auth";
import { useLocation } from "react-router-dom";
import { Clock } from "lucide-react";
import { SidebarTrigger } from "@/shared/presentation/ui/sidebar";

const routeTitles: Record<
  string,
  { title: string; subtitle?: string; icon?: string }
> = {
  "/admin/dashboard": {
    title: "Dashboard",
    subtitle: "Tổng quan hệ thống",
    icon: "📊",
  },
  "/admin/company": {
    title: "Quản lý công ty",
    subtitle: "Danh sách và thông tin công ty",
    icon: "🏢",
  },
  "/admin/user-manager": {
    title: "Quản lý người dùng",
    subtitle: "Tài khoản và phân quyền",
    icon: "👥",
  },
  "/admin/recruitment/job-manager": {
    title: "Quản lý việc làm",
    subtitle: "Đăng tải và quản lý job",
    icon: "💼",
  },
  "/admin/recruitment/job-manager/upsert": {
    title: "Thêm việc làm",
    subtitle: "Tạo job posting mới",
    icon: "➕",
  },
  "/admin/recruitment/skill-manager": {
    title: "Quản lý kỹ năng",
    subtitle: "Danh sách kỹ năng",
    icon: "🛠️",
  },
  "/admin/resume": {
    title: "Quản lý CV",
    subtitle: "Hồ sơ ứng viên",
    icon: "📄",
  },
  "/admin/access-control/permission": {
    title: "Quyền quản trị",
    subtitle: "Quản lý quyền quản trị hệ thống",
    icon: "🔐",
  },
  "/admin/access-control/role": {
    title: "Cấu hình quyền",
    subtitle: "Cấu hình quyền quản trị",
    icon: "🔐",
  },
};

export function AdminTopBar() {
  const { isLogin } = useAuthState();
  const location = useLocation();

  const currentRoute = routeTitles[location.pathname] || {
    title: "Admin",
    subtitle: "Quản trị hệ thống",
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
            {new Date().toLocaleTimeString("vi-VN", {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </span>
        </div>
      </div>

      {/* Right side */}
      {isLogin && (
        <div className="flex items-center gap-3">
          <UserMenu blackTheme />
        </div>
      )}
    </header>
  );
}
