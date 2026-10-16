import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the notification feature (namespace "notification"). */
const en = {
  bell: {
    title: "Notifications",
    markAllRead: "Mark all as read",
    empty: "No notifications yet",
    updateFailed: "Could not update the notification",
  },
} as const;

const vi: Translation<typeof en> = {
  bell: {
    title: "Thông báo",
    markAllRead: "Đánh dấu đã đọc",
    empty: "Chưa có thông báo nào",
    updateFailed: "Không thể cập nhật thông báo",
  },
};

export const messages = { en, vi };
