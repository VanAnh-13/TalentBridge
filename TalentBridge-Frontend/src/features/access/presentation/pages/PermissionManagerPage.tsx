import { errorMessage } from "@/shared/domain/errors";
import { permissions as permissionUseCases } from "@/app/container";
import type { Permission, PermissionData } from "../../domain/permission";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { PermissionSearchSection } from "../components/PermissionSearchSection";
import { Button } from "@/shared/presentation/ui/button";
import { Plus } from "lucide-react";
import Pagination from "@/shared/presentation/components/Pagination";
import { PermissionTable } from "../components/PermissionTable";
import { PermissionForm } from "../components/PermissionForm";

const PermissionManagerPage = () => {
  // Data
  const [permissions, setPermissions] = useState<
    Permission[]
  >([]);
  const [isLoading, setIsLoading] = useState(false);

  // Search
  const [searchName, setSearchName] = useState("");
  const [searchApiPath, setsearchApiPath] = useState("");

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // ============================
  // Dialog State
  // ============================
  const [isDialogOpen, setisDialogOpen] = useState(false);
  const [selectedPermission, setSelectedPermission] =
    useState<Permission | null>(null);

  const handleOpenCreateForm = () => {
    setSelectedPermission(null);
    setisDialogOpen(true);
  };

  const handleOpenEditForm = (permission: Permission) => {
    setSelectedPermission(permission);
    setisDialogOpen(true);
  };

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchPermissions = async (
    page: number,
    size: number,
    searchName: string,
    searchApiPath: string,
  ) => {
    setIsLoading(true);

    try {
      const res = await permissionUseCases.search(
        { name: searchName, apiPath: searchApiPath },
        page,
        size,
      );
      setPermissions(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể lấy danh sách công ty."));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPermissions(1, itemsPerPage, searchName, searchApiPath);
    setCurrentPage(1);
  }, [itemsPerPage, searchName, searchApiPath]);

  useEffect(() => {
    fetchPermissions(currentPage, itemsPerPage, searchName, searchApiPath);
  }, [currentPage, itemsPerPage, searchName, searchApiPath]);

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setSearchName("");
    setsearchApiPath("");
    setCurrentPage(1);
  };

  // ============================
  // HANDLE CREATE OR UPDATE
  // ============================
  const handleSubmitUpsert = async (
    data: PermissionData,
    id?: number,
  ) => {
    try {
      setIsLoading(true);

      if (id) {
        await permissionUseCases.update(id, data);
        await fetchPermissions(1, itemsPerPage, searchName, searchApiPath);
        toast.success("Cập nhật quyền hạn mới thành công");
      } else {
        await permissionUseCases.create(data);
        await fetchPermissions(1, itemsPerPage, searchName, searchApiPath);
        toast.success("Tạo quyền hạn mới thành công");
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

      await permissionUseCases.remove(id);
      await fetchPermissions(1, itemsPerPage, searchName, searchApiPath);

      toast.success("Xóa quyền hạn thành công");
    } catch (err) {
      toast.error(errorMessage(err, "Thao tác thất bại"));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <PermissionSearchSection
        searchName={searchName}
        setSearchName={setSearchName}
        searchApiPath={searchApiPath}
        setsearchApiPath={setsearchApiPath}
        onReset={handleReset}
      />

      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Danh sách Kỹ năng</h2>
        <Button
          className="bg-blue-600 hover:bg-blue-700"
          onClick={handleOpenCreateForm}
        >
          <Plus className="mr-2 h-4 w-4" />
          Thêm quyền hạn
        </Button>
      </div>

      <PermissionTable
        permissions={permissions}
        isLoading={isLoading}
        onDelete={handleDelete}
        onEdit={handleOpenEditForm}
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

      <PermissionForm
        open={isDialogOpen}
        onOpenChange={setisDialogOpen}
        initialData={selectedPermission}
        onSubmit={handleSubmitUpsert}
        onCloseForm={() => setSelectedPermission(null)}
      />
    </div>
  );
};

export default PermissionManagerPage;
