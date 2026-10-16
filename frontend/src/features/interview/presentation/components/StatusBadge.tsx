import { useTranslation } from "react-i18next";

import { Badge } from "@/shared/presentation/ui/badge";

import type { InterviewStatus } from "../../domain/interview";

/** Badge colour of each status; the label is the message interview:status.<STATUS>. */
const LOOK: Record<InterviewStatus, { className: string }> = {
  PROPOSED: { className: "bg-yellow-500" },
  CONFIRMED: { className: "bg-green-600" },
  DECLINED: { className: "bg-red-500" },
  CANCELLED: { className: "bg-gray-500" },
};

const StatusBadge = ({ status }: { status: InterviewStatus }) => {
  const { t } = useTranslation("interview");
  return <Badge className={`${LOOK[status].className} text-white`}>{t(`status.${status}`)}</Badge>;
};

export default StatusBadge;
