import { useAuthState } from "@/features/auth";
import { SidebarProvider } from "@/shared/presentation/ui/sidebar";
import { Navigate, Outlet } from "react-router-dom";
import { AdminTopBar } from "./Topbar";
import { AdminSidebar } from "./Sidebar";

const AdminPage = () => {
  const { isLogin } = useAuthState();

  if (!isLogin) return <Navigate to={"/home"} replace />;

  return (
    <SidebarProvider>
      <div className="flex min-h-screen w-full">
        <AdminSidebar />
        <div className="flex flex-1 flex-col">
          <AdminTopBar />
          <div className="flex-1 p-4">
            <Outlet />
          </div>
        </div>
      </div>
    </SidebarProvider>
  );
};

export default AdminPage;
