import { useAuthState } from "@/features/auth";
import { errorMessage } from "@/shared/domain/errors";
import type { Member } from "../../domain/company";
import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";
import { MemberTable } from "../components/MemberTable";
import { companies as companyUseCases } from "@/app/container";
import MemberInviteFrom from "../components/MemberInviteForm";
import { useTranslation } from "react-i18next";

const MemberManagePage = () => {
  const { user } = useAuthState();
  const { t } = useTranslation("company");

  // Data State
  const [isLoading, setIsLoading] = useState(false);
  const [members, setMembers] = useState<Member[]>([]);
  const [isOwner, setIsOwner] = useState(false);

  // ============================
  // HANDLE FETCHING DATA
  // ============================
  const fetchMembers = useCallback(async () => {
    setIsLoading(true);
    try {
      const res = await companyUseCases.members();
      setMembers(res);

      const owner = res.find((m) => m.owner);
      setIsOwner(owner?.email === user?.email);
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    } finally {
      setIsLoading(false);
    }
  }, [t, user?.email]);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]);

  // ============================
  // HANDLE ADD MEMBER
  // ============================
  const addMember = async (email: string) => {
    setIsLoading(true);
    try {
      await companyUseCases.addMember(email);
      await fetchMembers();
      toast.success(t("members.toasts.added"));
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  // ============================
  // HANDLE REMOVE MEMBER
  // ============================
  const removeMember = async (email: string) => {
    setIsLoading(true);
    try {
      await companyUseCases.removeMember(email);
      await fetchMembers();
      toast.success(t("members.toasts.removed"));
    } catch (err) {
      toast.error(errorMessage(err, t("errors.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-800">
          {t("members.title")}
        </h2>
      </div>

      <div className="rounded-lg bg-blue-50/50 px-6 py-4 shadow">
        <MemberInviteFrom onSubmit={addMember} />
        <p className="mt-2 text-sm text-gray-500">
          {t("members.inviteHint")}
        </p>
      </div>

      <div>
        <MemberTable
          isLoading={isLoading}
          users={members}
          onDelete={removeMember}
          isOwner={isOwner}
        />
      </div>

      <div className="flex items-center gap-2 rounded-lg bg-purple-50 px-4 py-3">
        <span className="font-bold text-purple-700">{t("members.noteLabel")}</span>
        <span>{t("members.note")}</span>
      </div>
    </div>
  );
};

export default MemberManagePage;
