import { Badge } from "@/shared/presentation/ui/badge";

import type { InterviewStatus } from "../../domain/interview";

const LOOK: Record<InterviewStatus, { label: string; className: string }> = {
  PROPOSED: { label: "Chờ ứng viên chọn", className: "bg-yellow-500" },
  CONFIRMED: { label: "Đã xác nhận", className: "bg-green-600" },
  DECLINED: { label: "Ứng viên từ chối", className: "bg-red-500" },
  CANCELLED: { label: "Đã hủy", className: "bg-gray-500" },
};

const StatusBadge = ({ status }: { status: InterviewStatus }) => (
  <Badge className={`${LOOK[status].className} text-white`}>{LOOK[status].label}</Badge>
);

export default StatusBadge;
