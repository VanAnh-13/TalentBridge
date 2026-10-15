import { permissions as permissionUseCases, roles as roleUseCases } from "@/app/container";
import type { Permission } from "../../domain/permission";
import type { Role, RoleData } from "../../domain/role";
import { useEffect, useState } from "react";
import { RoleSearchSection } from "../components/RoleSearchSection.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { Plus } from "lucide-react";
import Pagination from "@/shared/presentation/components/Pagination.tsx";
import { toast } from "sonner";
import { errorMessage } from "@/shared/domain/errors";
import { RoleTable } from "../components/RoleTable.tsx";
import { RoleForm } from "../components/RoleForm.tsx";
import { HasPermission } from "@/features/auth";

const RoleManagerPage = () => {
  // Data
  const [roles, setRoles] = useState<Role[]>([]);
  const [permissions, setPermissions] = useState<
    Permission[]
  >([]);
  const [isLoading, setIsLoading] = useState(false);

  // Search
  const [searchRoleName, setSearchRoleName] = useState("");

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // ============================
  // Dialog State
  // ============================
  const [isDialogOpen, setisDialogOpen] = useState(false);
  const [selectedRole, setSelectedRole] =
    useState<Role | null>(null);

  const handleOpenCreateForm = () => {
    setSelectedRole(null);
    setisDialogOpen(true);
  };

  const handleOpenEditForm = (role: Role) => {
    setSelectedRole(role);
    setisDialogOpen(true);
  };

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchRoles = async (
    page: number,
    size: number,
    searchRoleName: string,
  ) => {
    setIsLoading(true);

    try {
      const res = await roleUseCases.search(searchRoleName, page, size);
      setRoles(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể lấy danh sách công ty."));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRoles(1, itemsPerPage, searchRoleName);
    setCurrentPage(1);
  }, [itemsPerPage, searchRoleName]);

  useEffect(() => {
    fetchRoles(currentPage, itemsPerPage, searchRoleName);
  }, [currentPage, itemsPerPage, searchRoleName]);

  useEffect(() => {
    const fetchPermissions = async () => {
      setIsLoading(true);

      try {
        setPermissions(await permissionUseCases.catalog());
      } catch (err) {
        toast.error(errorMessage(err, "Không thể lấy danh sách công ty."));
      } finally {
        setIsLoading(false);
      }
    };

    fetchPermissions();
  }, []);

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setSearchRoleName("");
    setCurrentPage(1);
  };

  // ============================
  // HANDLE CREATE OR UPDATE
  // ============================
  const handleSubmitUpsert = async (
    data: RoleData,
    id?: number,
  ) => {
    try {
      setIsLoading(true);

      if (id) {
        await roleUseCases.update(id, data);
        await fetchRoles(1, itemsPerPage, searchRoleName);
        toast.success("Cập nhật chức vụ mới thành công");
      } else {
        await roleUseCases.create(data);
        await fetchRoles(1, itemsPerPage, searchRoleName);
        toast.success("Tạo chức vụ mới thành công");
      }
    } catch (err) {
      toast.error(errorMessage(err, "Thao tác thất bại"));
    } finally {
      setIsLoading(false);
    }
  };

  // ============================
  // HANDLE DELETE
  // ============================
  const handleDelete = async (id: number) => {
    try {
      setIsLoading(true);

      await roleUseCases.remove(id);
      await fetchRoles(1, itemsPerPage, searchRoleName);

      toast.success("Xóa chức vụ thành công");
    } catch (err) {
      toast.error(errorMessage(err, "Thao tác thất bại"));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <RoleSearchSection
        searchRoleName={searchRoleName}
        setSearchRoleName={setSearchRoleName}
        onReset={handleReset}
      />

      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Danh sách chức vụ</h2>
        <HasPermission perm={"POST /roles"}>
          <Button
            className="bg-blue-600 hover:bg-blue-700"
            onClick={handleOpenCreateForm}
          >
            <Plus className="mr-2 h-4 w-4" />
            Thêm chức vụ
          </Button>
        </HasPermission>
      </div>

      <RoleTable
        roles={roles}
        isLoading={isLoading}
        onEdit={handleOpenEditForm}
        onDelete={handleDelete}
      />

      <Pagination
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsPerPage={itemsPerPage}
        setItemsPerPage={setItemsPerPage}
        showItemsPerPageSelect={true}
      />

      <RoleForm
        open={isDialogOpen}
        onOpenChange={setisDialogOpen}
        initialData={selectedRole}
        onSubmit={handleSubmitUpsert}
        onCloseForm={() => setSelectedRole(null)}
        permissions={permissions}
      />
    </div>
  );
};

export default RoleManagerPage;
