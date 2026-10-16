import type { Translation } from "./types";

/** Words and phrases shared by every part of the interface (namespace "common"). */
const en = {
  actions: {
    cancel: "Cancel",
    delete: "Delete",
    edit: "Edit",
    update: "Update",
    add: "Add",
    reset: "Reset",
    refresh: "Refresh",
    confirm: "Confirm",
    send: "Send",
    more: "More",
    less: "Less",
  },
  states: {
    loading: "Loading...",
    active: "Active",
    inactive: "Inactive",
  },
  fields: {
    id: "ID",
    name: "Name",
    email: "Email",
    password: "Password",
    description: "Description",
    address: "Address",
    status: "Status",
    actions: "Actions",
    createdAt: "Created",
    updatedAt: "Last updated",
    company: "Company",
    job: "Job",
    logo: "Logo",
  },
  pagination: {
    previous: "Previous",
    next: "Next",
    range: "{{from}} – {{to}} of {{total}} rows",
  },
  deleteDialog: {
    title: "Are you sure you want to delete this?",
    description: "This cannot be undone.",
    confirm: "Delete",
  },
  errorPage: {
    title: "Oops! Something went wrong.",
    description: "Sorry, the page you asked for could not be found or an unexpected error happened.",
  },
  salary: {
    negotiable: "Negotiable",
  },
  time: {
    justNow: "Just now",
  },
  resumeStatus: {
    PENDING: "Pending",
    REVIEWING: "Reviewing",
    APPROVED: "Approved",
    REJECTED: "Rejected",
  },
  levels: {
    INTERN: "Intern",
    FRESHER: "Fresher",
    MIDDLE: "Middle",
    SENIOR: "Senior",
  },
  language: {
    label: "Language",
    en: "English",
    vi: "Tiếng Việt",
  },
  pdf: {
    title: "CV (PDF)",
  },
  editor: {
    placeholder: "Enter a description...",
    imageUrlTitle: "Image URL",
    insertImage: "Insert image",
  },
  errors: {
    generic: "Something went wrong. Please try again.",
  },
} as const;

const vi: Translation<typeof en> = {
  actions: {
    cancel: "Hủy",
    delete: "Xóa",
    edit: "Sửa",
    update: "Cập nhật",
    add: "Thêm",
    reset: "Làm lại",
    refresh: "Làm mới",
    confirm: "Xác nhận",
    send: "Gửi",
    more: "Mở rộng",
    less: "Thu gọn",
  },
  states: {
    loading: "Đang tải...",
    active: "Đang hoạt động",
    inactive: "Ngừng hoạt động",
  },
  fields: {
    id: "ID",
    name: "Tên",
    email: "Email",
    password: "Mật khẩu",
    description: "Mô tả",
    address: "Địa chỉ",
    status: "Trạng thái",
    actions: "Hành động",
    createdAt: "Ngày tạo",
    updatedAt: "Lần cập nhật gần nhất",
    company: "Công ty",
    job: "Công việc",
    logo: "Logo",
  },
  pagination: {
    previous: "Trước",
    next: "Sau",
    range: "{{from}} – {{to}} trên {{total}} dòng",
  },
  deleteDialog: {
    title: "Bạn có chắc chắn muốn xóa?",
    description: "Hành động này không thể hoàn tác.",
    confirm: "Xác nhận xóa",
  },
  errorPage: {
    title: "Rất tiếc! Đã xảy ra sự cố.",
    description: "Chúng tôi xin lỗi, nhưng trang bạn yêu cầu không thể tìm thấy hoặc đã xảy ra một lỗi bất ngờ.",
  },
  salary: {
    negotiable: "Thương lượng",
  },
  time: {
    justNow: "Vừa xong",
  },
  resumeStatus: {
    PENDING: "Chờ duyệt",
    REVIEWING: "Đang xét duyệt",
    APPROVED: "Đã duyệt",
    REJECTED: "Từ chối",
  },
  levels: {
    INTERN: "Thực tập",
    FRESHER: "Fresher",
    MIDDLE: "Middle",
    SENIOR: "Senior",
  },
  language: {
    label: "Ngôn ngữ",
    en: "English",
    vi: "Tiếng Việt",
  },
  pdf: {
    title: "Xem hồ sơ (PDF)",
  },
  editor: {
    placeholder: "Nhập mô tả...",
    imageUrlTitle: "Nhập URL ảnh",
    insertImage: "Chèn ảnh",
  },
  errors: {
    generic: "Đã có lỗi xảy ra. Vui lòng thử lại.",
  },
};

export const commonMessages = { en, vi };
