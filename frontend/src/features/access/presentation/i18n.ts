import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the access feature (namespace "access"). */
const en = {
  actions: {
    saveChanges: "Save changes",
  },
  errors: {
    operationFailed: "Operation failed",
  },
  permission: {
    fields: {
      name: "Permission name",
      apiPath: "API path",
      method: "Method",
      module: "Module",
    },
    list: {
      title: "Permissions",
      add: "Add permission",
      emptyTitle: "No permissions found",
      emptyDescription: "Try different search criteria or add a new permission",
      loadFailed: "Could not load the permissions.",
      created: "Permission created",
      updated: "Permission updated",
      deleted: "Permission deleted",
    },
    search: {
      name: "Permission name:",
      namePlaceholder: "Enter a permission name...",
      apiPathPlaceholder: "Enter an API path...",
    },
    form: {
      title: "API permission",
      createDescription: "Add a new permission",
      editDescription: "Edit the permission",
      name: "Permission name",
      apiPath: "API path",
      methodRequired: "Please choose a method",
      modulePlaceholder: "Select a module",
    },
  },
  role: {
    fields: {
      name: "Role name",
    },
    list: {
      title: "Roles",
      add: "Add role",
      emptyTitle: "No roles found",
      emptyDescription: "Try different search criteria or add a new role",
      loadFailed: "Could not load the roles.",
      created: "Role created",
      updated: "Role updated",
      deleted: "Role deleted",
    },
    search: {
      namePlaceholder: "Enter a role name...",
    },
    form: {
      title: "Role details",
      createDescription: "Add a new role",
      editDescription: "Edit the role",
      defaultNameLocked: "A default role cannot be renamed",
      defaultDescriptionLocked: "The description of a default role cannot be changed",
      permissions: "Permissions",
      permissionsHint: "Choose the permissions of this role",
    },
  },
  roleSelection: {
    label: "Role",
    none: "No role selected",
    choose: "Choose a role",
    change: "Change role",
    dialogDescription: "Role selection panel",
    searchPlaceholder: "Search roles...",
    emptyTitle: "No roles found",
    emptyDescription: "Try different search criteria or add a new role",
    loadFailed: "Could not load the roles.",
  },
} as const;

const vi: Translation<typeof en> = {
  actions: {
    saveChanges: "Lưu thay đổi",
  },
  errors: {
    operationFailed: "Thao tác thất bại",
  },
  permission: {
    fields: {
      name: "Tên quyền hạn",
      apiPath: "Đường dẫn API",
      method: "Phương thức",
      module: "Module",
    },
    list: {
      title: "Danh sách quyền hạn",
      add: "Thêm quyền hạn",
      emptyTitle: "Không tìm thấy quyền hạn nào",
      emptyDescription: "Thử thay đổi tiêu chí tìm kiếm hoặc thêm quyền hạn mới",
      loadFailed: "Không thể lấy danh sách quyền hạn.",
      created: "Tạo quyền hạn mới thành công",
      updated: "Cập nhật quyền hạn thành công",
      deleted: "Xóa quyền hạn thành công",
    },
    search: {
      name: "Tên phân quyền:",
      namePlaceholder: "Nhập tên phân quyền...",
      apiPathPlaceholder: "Nhập đường dẫn API...",
    },
    form: {
      title: "Biểu mẫu quyền hạn API",
      createDescription: "Thêm quyền hạn mới",
      editDescription: "Chỉnh sửa quyền hạn",
      name: "Tên quyền",
      apiPath: "API Path",
      methodRequired: "Vui lòng chọn phương thức",
      modulePlaceholder: "Chọn module",
    },
  },
  role: {
    fields: {
      name: "Tên chức vụ",
    },
    list: {
      title: "Danh sách chức vụ",
      add: "Thêm chức vụ",
      emptyTitle: "Không tìm thấy chức vụ nào",
      emptyDescription: "Thử thay đổi tiêu chí tìm kiếm hoặc thêm chức vụ mới",
      loadFailed: "Không thể lấy danh sách chức vụ.",
      created: "Tạo chức vụ mới thành công",
      updated: "Cập nhật chức vụ thành công",
      deleted: "Xóa chức vụ thành công",
    },
    search: {
      namePlaceholder: "Nhập tên chức vụ...",
    },
    form: {
      title: "Biểu mẫu thông tin chức vụ",
      createDescription: "Thêm chức vụ mới",
      editDescription: "Chỉnh sửa chức vụ",
      defaultNameLocked: "Chức vụ mặc định không được phép đổi tên",
      defaultDescriptionLocked: "Chức vụ mặc định không được phép đổi mô tả",
      permissions: "Quyền hạn",
      permissionsHint: "Chọn các quyền hạn cho chức vụ này",
    },
  },
  roleSelection: {
    label: "Vai trò",
    none: "Chưa chọn vai trò",
    choose: "Chọn vai trò",
    change: "Đổi vai trò",
    dialogDescription: "Bảng chọn vai trò",
    searchPlaceholder: "Tìm kiếm vai trò...",
    emptyTitle: "Không tìm thấy vai trò nào",
    emptyDescription: "Thử thay đổi tiêu chí tìm kiếm hoặc thêm vai trò mới",
    loadFailed: "Không thể lấy danh sách vai trò.",
  },
};

export const messages = { en, vi };
