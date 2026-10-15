import { CalendarCheck, CalendarPlus, MapPin, Video } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

import { interviews as interviewUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import LoadingSpinner from "@/shared/presentation/components/LoadingSpinner";
import Pagination from "@/shared/presentation/components/Pagination";
import { formatISO } from "@/shared/presentation/format";
import { Button } from "@/shared/presentation/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/presentation/ui/card";

import { isActive, isAwaitingAnswer } from "../../domain/interview";
import type { Interview } from "../../domain/interview";
import StatusBadge from "../components/StatusBadge";
import { downloadCalendar } from "../components/downloadCalendar";

/** The candidate's invitations: pick a slot, decline, or add a confirmed interview to the calendar. */
const MyInterviewsPage = () => {
  const [items, setItems] = useState<Interview[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [picked, setPicked] = useState<Record<number, string>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const result = await interviewUseCases.mine(page, size);
      setItems(result.content);
      setTotalPages(result.totalPages);
      setTotalElements(result.totalElements);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể tải lời mời phỏng vấn"));
    } finally {
      setLoading(false);
    }
  }, [page, size]);

  useEffect(() => {
    void load();
  }, [load]);

  const act = async (work: () => Promise<unknown>, success: string, failure: string) => {
    try {
      await work();
      toast.success(success);
      void load();
    } catch (err) {
      toast.error(errorMessage(err, failure));
    }
  };

  if (loading && items.length === 0) return <LoadingSpinner />;

  return (
    <div className="mx-auto max-w-4xl space-y-4 px-4 py-6">
      <h1 className="text-2xl font-bold text-gray-900">
        Lời mời phỏng vấn {totalElements > 0 && `(${totalElements})`}
      </h1>

      {items.length === 0 ? (
        <EmptyState icon={<CalendarCheck className="h-10 w-10" />} title="Chưa có lời mời phỏng vấn" description="Nhà tuyển dụng sẽ gửi lời mời tại đây." />
      ) : (
        items.map((interview) => (
          <Card key={interview.id}>
            <CardHeader className="flex flex-row items-start justify-between gap-2">
              <div>
                <CardTitle className="text-lg">{interview.jobName}</CardTitle>
                <p className="text-sm text-gray-600">{interview.companyName}</p>
              </div>
              <StatusBadge status={interview.status} />
            </CardHeader>
            <CardContent className="space-y-3">
              <p className="flex items-center gap-2 text-sm">
                {interview.mode === "ONLINE" ? <Video className="h-4 w-4" /> : <MapPin className="h-4 w-4" />}
                {interview.location} · {interview.durationMinutes} phút
              </p>
              {interview.note && <p className="text-sm text-gray-600">Ghi chú: {interview.note}</p>}

              {isAwaitingAnswer(interview) && (
                <fieldset className="space-y-2">
                  <legend className="text-sm font-medium">Chọn một khung giờ:</legend>
                  {interview.proposedSlots.map((slot) => (
                    <label key={slot} className="flex items-center gap-2 text-sm">
                      <input
                        type="radio"
                        name={`slot-${interview.id}`}
                        checked={picked[interview.id] === slot}
                        onChange={() => setPicked({ ...picked, [interview.id]: slot })}
                      />
                      {formatISO(slot)}
                    </label>
                  ))}
                </fieldset>
              )}

              {interview.status === "CONFIRMED" && interview.confirmedSlot && (
                <p className="text-sm font-medium text-green-700">Lịch hẹn: {formatISO(interview.confirmedSlot)}</p>
              )}

              <div className="flex flex-wrap gap-2">
                {isAwaitingAnswer(interview) && (
                  <Button
                    disabled={!picked[interview.id]}
                    onClick={() =>
                      act(
                        () => interviewUseCases.confirm(interview.id, picked[interview.id]),
                        "Đã xác nhận lịch phỏng vấn",
                        "Không thể xác nhận",
                      )
                    }
                  >
                    Xác nhận
                  </Button>
                )}
                {interview.status === "CONFIRMED" && (
                  <Button
                    variant="outline"
                    onClick={() => act(() => downloadCalendar(interview.id), "Đã tải lịch", "Không thể tải lịch")}
                  >
                    <CalendarPlus className="mr-1 h-4 w-4" /> Thêm vào lịch
                  </Button>
                )}
                {isActive(interview) && (
                  <Button
                    variant="outline"
                    className="text-red-600"
                    onClick={() => act(() => interviewUseCases.decline(interview.id), "Đã từ chối buổi phỏng vấn", "Không thể từ chối")}
                  >
                    Từ chối
                  </Button>
                )}
              </div>
            </CardContent>
          </Card>
        ))
      )}

      <Pagination
        currentPage={page}
        setCurrentPage={setPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsPerPage={size}
        setItemsPerPage={setSize}
        showItemsPerPageSelect={false}
      />
    </div>
  );
};

export default MyInterviewsPage;
