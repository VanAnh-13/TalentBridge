import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the subscriber feature (namespace "subscriber"). */
const en = {
  page: {
    title: "Job alerts",
    subtitle: "Get notified about jobs that match your skills",
  },
  current: {
    title: "Subscription status",
    description: "You get alerts for the following skills",
    skills: "Subscribed skills ({{count}})",
    unsubscribe: "Unsubscribe",
    unsubscribeConfirm: "Are you sure you want to unsubscribe?",
  },
  none: {
    title: "You are not subscribed to job alerts",
    description: "Subscribe now to hear about jobs that match your skills",
    subscribe: "Subscribe now",
  },
  form: {
    editTitle: "Edit your subscription",
    createTitle: "Subscribe to job alerts",
    editDescription: "Update your skills to get better-matched alerts",
    createDescription: "Choose skills to hear about matching jobs",
    hint: "Choose the skills you are interested in. We will notify you when a job asks for them.",
    subscribe: "Subscribe",
    skillRequired: "Please choose at least one skill to continue.",
  },
  toast: {
    skillRequired: "Please choose at least one skill",
    updated: "Subscription updated",
    subscribed: "You are now subscribed to job alerts",
    unsubscribed: "You have unsubscribed",
    failed: "The operation failed",
  },
} as const;

const vi: Translation<typeof en> = {
  page: {
    title: "Đăng ký nhận thông báo việc làm",
    subtitle: "Nhận thông báo về các công việc phù hợp với kỹ năng của bạn",
  },
  current: {
    title: "Trạng thái đăng ký",
    description: "Bạn đang nhận thông báo cho các kỹ năng sau",
    skills: "Kỹ năng đã đăng ký ({{count}})",
    unsubscribe: "Hủy đăng ký",
    unsubscribeConfirm: "Bạn chắc chắn muốn hủy đăng ký không?",
  },
  none: {
    title: "Chưa đăng ký nhận thông báo",
    description: "Đăng ký ngay để nhận thông báo về các công việc phù hợp với kỹ năng của bạn",
    subscribe: "Đăng ký ngay",
  },
  form: {
    editTitle: "Chỉnh sửa đăng ký",
    createTitle: "Đăng ký nhận thông báo",
    editDescription: "Cập nhật kỹ năng để nhận thông báo phù hợp hơn",
    createDescription: "Chọn kỹ năng để nhận thông báo về các công việc phù hợp",
    hint: "Chọn các kỹ năng mà bạn quan tâm. Hệ thống sẽ gửi thông báo khi có công việc yêu cầu những kỹ năng này.",
    subscribe: "Đăng ký",
    skillRequired: "Vui lòng chọn ít nhất một kỹ năng để tiếp tục.",
  },
  toast: {
    skillRequired: "Vui lòng chọn ít nhất một kỹ năng",
    updated: "Cập nhật đăng ký thành công",
    subscribed: "Đăng ký nhận thông báo thành công",
    unsubscribed: "Hủy đăng ký thành công",
    failed: "Thao tác thất bại",
  },
};

export const messages = { en, vi };
