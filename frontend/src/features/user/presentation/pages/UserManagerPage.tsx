import { errorMessage } from "@/shared/domain/errors";
import { users as userUseCases } from "@/app/container";
import type { User } from "../../domain/user";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { UserSearchSection } from "../components/UserSearchSection.tsx";
import { Button } from "@/shared/presentation/ui/button.tsx";
import { Plus } from "lucide-react";
import Pagination from "@/shared/presentation/components/Pagination.tsx";
import { UserTable } from "../components/UserTable.tsx";
import { useNavigate } from "react-router-dom";
import { HasPermission } from "@/features/auth";
import { useTranslation } from "react-i18next";
import i18n from "i18next";

const UserManagerPage = () => {
  const { t } = useTranslation("user");
  const navigate = useNavigate();

  // ============================
  // Data
  // ============================
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // ============================
  // Pagination State
  // ============================
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // ============================
  // Search State
  // ============================
  const [searchUserName, setSearchUserName] = useState("");
  const [searchEmail, setSearchEmail] = useState("");

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchUsers = async (
    page: number,
    size: number,
    searchUserName: string,
    searchEmail: string,
  ) => {
    setIsLoading(true);
    try {
      const res = await userUseCases.search(
        { name: searchUserName, email: searchEmail },
        page,
        size,
      );
      setUsers(res.content);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      // i18n.t rather than t: fetchUsers stays free of render values, so the effects below need no extra dependency
      toast.error(errorMessage(err, i18n.t("list.loadFailed", { ns: "user" })));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers(currentPage, itemsPerPage, searchUserName, searchEmail);
  }, [currentPage, itemsPerPage, searchUserName, searchEmail]);

  useEffect(() => {
    fetchUsers(1, itemsPerPage, searchUserName, searchEmail);
    setCurrentPage(1);
  }, [itemsPerPage, searchUserName, searchEmail]);

  // ============================
  // HANDLE NAVIGATE UPSERT PAGE
  // ============================
  const handleOpenCreatePage = () => {
    navigate("upsert");
  };

  const handleOpenEditPage = (id: number) => {
    navigate(`upsert?id=${id}`);
  };

  // ============================
  // HANDLE RESET
  // ============================
  const handleReset = () => {
    setSearchUserName("");
    setSearchEmail("");
    setCurrentPage(1);
  };

  // ============================
  // HANDLE DELETE
  // ============================
  const handleDelete = async (id: number) => {
    try {
      setIsLoading(true);

      await userUseCases.remove(id);
      await fetchUsers(1, itemsPerPage, searchUserName, searchEmail);

      toast.success(t("list.deleted"));
    } catch (err) {
      toast.error(errorMessage(err, t("errors.operationFailed")));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <UserSearchSection
        searchUserName={searchUserName}
        setSearchUserName={setSearchUserName}
        searchEmail={searchEmail}
        setSearchEmail={setSearchEmail}
        onReset={handleReset}
      />

      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">{t("list.title")}</h2>
        <HasPermission perm={"POST /users"}>
          <Button
            onClick={handleOpenCreatePage}
            className="bg-blue-600 hover:bg-blue-700"
          >
            <Plus className="mr-2 h-4 w-4" />
            {t("list.add")}
          </Button>
        </HasPermission>
      </div>

      <UserTable
        users={users}
        isLoading={isLoading}
        onEdit={handleOpenEditPage}
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
    </div>
  );
};

export default UserManagerPage;
