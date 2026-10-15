import { CalendarDays, CalendarPlus, XCircle } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { interviews as interviewUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import Pagination from "@/shared/presentation/components/Pagination";
import { formatISO } from "@/shared/presentation/format";
import { Button } from "@/shared/presentation/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/presentation/ui/table";

import { isActive } from "../../domain/interview";
import type { Interview } from "../../domain/interview";
import ScheduleInterviewDialog from "../components/ScheduleInterviewDialog";
import StatusBadge from "../components/StatusBadge";
import { downloadCalendar } from "../components/downloadCalendar";

/** The company's interviews: follow the answers, move or cancel. */
const RecruiterInterviewsPage = () => {
  const [items, setItems] = useState<Interview[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [moving, setMoving] = useState<Interview | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const result = await interviewUseCases.ofMyCompany(page, size);
      setItems(result.content);
      setTotalPages(result.totalPages);
      setTotalElements(result.totalElements);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể tải danh sách phỏng vấn"));
    } finally {
      setLoading(false);
    }
  }, [page, size]);

  useEffect(() => {
    void load();
  }, [load]);

  const cancel = async (interview: Interview) => {
    try {
      await interviewUseCases.cancel(interview.id);
      toast.success("Đã hủy buổi phỏng vấn");
      void load();
    } catch (err) {
      toast.error(errorMessage(err, "Không thể hủy buổi phỏng vấn"));
    }
  };

  const saveCalendar = async (interview: Interview) => {
    try {
      await downloadCalendar(interview.id);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể tải lịch"));
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-lg font-semibold">Lịch phỏng vấn</h2>

      <div className="overflow-hidden rounded-lg border border-purple-600">
        <Table>
          <TableHeader className="bg-purple-600 text-white">
            <TableRow>
              <TableHead className="text-center font-bold text-white">Ứng viên</TableHead>
              <TableHead className="text-center font-bold text-white">Vị trí</TableHead>
              <TableHead className="text-center font-bold text-white">Thời gian</TableHead>
              <TableHead className="text-center font-bold text-white">Hình thức</TableHead>
              <TableHead className="text-center font-bold text-white">Trạng thái</TableHead>
              <TableHead className="text-center font-bold text-white">Thao tác</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={6}>
                  <LoadingSpinner />
                </TableCell>
              </TableRow>
            ) : items.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6}>
                  <EmptyState icon={<CalendarDays className="h-10 w-10" />} title="Chưa có buổi phỏng vấn nào" description="Mời phỏng vấn từ trang Hồ sơ ứng tuyển." />
                </TableCell>
              </TableRow>
            ) : (
              items.map((interview) => (
                <TableRow key={interview.id}>
                  <TableCell className="text-center">{interview.candidateEmail}</TableCell>
                  <TableCell className="text-center">{interview.jobName}</TableCell>
                  <TableCell className="text-center">
                    {interview.confirmedSlot ? (
                      formatISO(interview.confirmedSlot)
                    ) : interview.status === "PROPOSED" ? (
                      <span className="text-sm text-gray-600">{interview.proposedSlots.length} khung giờ đề xuất</span>
                    ) : (
                      "—"
                    )}
                  </TableCell>
                  <TableCell className="text-center">{interview.mode === "ONLINE" ? "Trực tuyến" : "Trực tiếp"}</TableCell>
                  <TableCell className="text-center">
                    <StatusBadge status={interview.status} />
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center justify-center gap-1">
                      {interview.status === "CONFIRMED" && (
                        <Button size="icon" variant="ghost" aria-label="Tải lịch" onClick={() => saveCalendar(interview)}>
                          <CalendarPlus className="h-4 w-4" />
                        </Button>
                      )}
                      {interview.status !== "CANCELLED" && (
                        <Button size="sm" variant="outline" onClick={() => setMoving(interview)}>
                          Dời lịch
                        </Button>
                      )}
                      {isActive(interview) && (
                        <Button size="icon" variant="ghost" aria-label="Hủy" className="text-red-500" onClick={() => cancel(interview)}>
                          <XCircle className="h-4 w-4" />
                        </Button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <Pagination
        currentPage={page}
        setCurrentPage={setPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsPerPage={size}
        setItemsPerPage={setSize}
        showItemsPerPageSelect={true}
        theme="purple"
      />

      <ScheduleInterviewDialog
        open={moving !== null}
        onOpenChange={(open) => !open && setMoving(null)}
        interview={moving ?? undefined}
        onDone={() => void load()}
      />
    </div>
  );
};

export default RecruiterInterviewsPage;
