import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the chat feature (namespace "chat"). */
const en = {
  title: "Messages",
  noConversations: "No conversations yet. Start one from an application.",
  selectTitle: "Select a conversation",
  selectText: "Messages will show up here.",
  loadOlder: "Show older messages",
  seen: "Seen",
  sent: "Sent",
  placeholder: "Type a message...",
  messageLabel: "Message",
  you: "You",
  errors: {
    conversations: "Could not load your conversations",
    open: "Could not open the conversation",
    messages: "Could not load the messages",
    older: "Could not load older messages",
    send: "Could not send the message",
    invalid: "Invalid message",
  },
} as const;

const vi: Translation<typeof en> = {
  title: "Tin nhắn",
  noConversations: "Chưa có cuộc trò chuyện nào. Mở từ hồ sơ ứng tuyển.",
  selectTitle: "Chọn một cuộc trò chuyện",
  selectText: "Tin nhắn sẽ hiện ở đây.",
  loadOlder: "Xem tin nhắn cũ hơn",
  seen: "Đã xem",
  sent: "Đã gửi",
  placeholder: "Nhập tin nhắn...",
  messageLabel: "Tin nhắn",
  you: "Bạn",
  errors: {
    conversations: "Không thể tải danh sách trò chuyện",
    open: "Không thể mở cuộc trò chuyện",
    messages: "Không thể tải tin nhắn",
    older: "Không thể tải tin nhắn cũ",
    send: "Không thể gửi tin nhắn",
    invalid: "Tin nhắn không hợp lệ",
  },
};

export const messages = { en, vi };
