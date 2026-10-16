import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the skill feature (namespace "skill"). */
const en = {
  fields: {
    name: "Skill name",
    updatedAt: "Updated",
  },
  notFound: {
    title: "No skills found",
    description: "Try other search criteria or add a new skill",
  },
  errors: {
    loadList: "Could not load the skills.",
    failed: "The operation failed.",
    deleteFailed: "Could not delete the skill.",
  },
  toasts: {
    created: "Skill created.",
    updated: "Skill updated.",
    deleted: "Skill deleted.",
  },
  search: {
    name: "Skill name:",
    placeholder: "Enter a skill name...",
  },
  form: {
    title: "Skill information",
    editMode: "Edit the skill",
    createMode: "Add a new skill",
    saveChanges: "Save changes",
  },
  selection: {
    label: "Required skills",
    none: "No skills selected",
    edit: "Edit skills",
    choose: "Choose skills",
    title: "Choose the required skills",
    description: "Tick the skills to add",
    searchPlaceholder: "Search skills...",
  },
  manager: {
    title: "Skills",
    add: "Add a skill",
  },
} as const;

const vi: Translation<typeof en> = {
  fields: {
    name: "Tên kỹ năng",
    updatedAt: "Cập nhật",
  },
  notFound: {
    title: "Không tìm thấy kỹ năng nào",
    description: "Thử thay đổi tiêu chí tìm kiếm hoặc thêm kỹ năng mới",
  },
  errors: {
    loadList: "Không thể lấy danh sách kỹ năng.",
    failed: "Thao tác thất bại.",
    deleteFailed: "Xóa kỹ năng thất bại.",
  },
  toasts: {
    created: "Tạo kỹ năng thành công.",
    updated: "Cập nhật kỹ năng thành công.",
    deleted: "Xóa kỹ năng thành công.",
  },
  search: {
    name: "Tên kỹ năng:",
    placeholder: "Nhập tên kỹ năng...",
  },
  form: {
    title: "Biểu mẫu thông tin kỹ năng",
    editMode: "Chỉnh sửa kỹ năng",
    createMode: "Thêm kỹ năng mới",
    saveChanges: "Lưu thay đổi",
  },
  selection: {
    label: "Kỹ năng yêu cầu",
    none: "Chưa chọn kỹ năng",
    edit: "Chỉnh sửa kỹ năng",
    choose: "Chọn kỹ năng",
    title: "Chọn kỹ năng yêu cầu",
    description: "Đánh dấu các kỹ năng cần thêm",
    searchPlaceholder: "Tìm kiếm kỹ năng...",
  },
  manager: {
    title: "Danh sách Kỹ năng",
    add: "Thêm kỹ năng",
  },
};

export const messages = { en, vi };
