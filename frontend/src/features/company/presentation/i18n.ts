import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the company feature (namespace "company"). */
const en = {
  fields: {
    name: "Company name",
  },
  logoAlt: "{{name}} logo",
  dates: {
    created: "Created:",
    updated: "Updated:",
  },
  notFound: {
    title: "No companies found",
    description: "Try other search criteria or add a new company",
  },
  errors: {
    loadList: "Could not load the companies.",
    loadOne: "Could not load the company.",
    failed: "The operation failed.",
    deleteFailed: "Could not delete the company.",
  },
  toasts: {
    created: "Company added.",
    updated: "Company updated.",
    deleted: "Company deleted.",
  },
  deleteDialog: {
    title: "Are you sure you want to delete this company?",
    description:
      "This will <danger>delete the COMPANY, its JOBS and the APPLICATIONS</danger> submitted to those jobs.",
  },
  search: {
    name: "Company name:",
    namePlaceholder: "Enter a name",
    address: "Company address:",
    addressPlaceholder: "Enter an address",
    /** Shortcut buttons; `value` is the search term, spelt as the addresses are stored so it matches in either language. */
    cities: {
      hcm: { label: "HCM", value: "Hồ Chí Minh" },
      hanoi: { label: "HN", value: "Hà Nội" },
      danang: { label: "DN", value: "Đà Nẵng" },
    },
  },
  form: {
    title: "Company information",
    editMode: "Edit the company",
    createMode: "Add a new company",
    descriptionPlaceholder: "Describe the company...",
    logoPreview: "Logo preview",
    saveChanges: "Save changes",
  },
  details: {
    title: "Company details",
    breadcrumb: "Companies",
    current: "{{name}}",
  },
  selection: {
    none: "No company selected",
    change: "Change company",
    choose: "Choose a company",
    description: "Pick a company from the list",
    searchPlaceholder: "Search companies...",
  },
  jobs: {
    hiring_one: "Hiring for <highlight>{{count}} job</highlight>",
    hiring_other: "Hiring for <highlight>{{count}} jobs</highlight>",
    location: "Location:",
    salary: "Salary:",
    quantity: "Openings:",
    level: "Level:",
    period: "Period:",
  },
  grid: {
    jobs_one: "{{count}} job",
    jobs_other: "{{count}} jobs",
  },
  top: {
    title: "Top companies hiring now",
    subtitle: "Discover leading companies with great places to work",
    emptyDescription: "Something may have gone wrong",
    viewJobs: "View jobs",
  },
  browse: {
    title: "Companies",
    subtitle: "Discover leading companies and career opportunities",
    stats: {
      companies: "Companies",
      jobs: "Jobs",
      candidates: "Candidates",
    },
    found_one: "Found {{count}} company",
    found_other: "Found {{count}} companies",
  },
  manager: {
    title: "Companies",
    add: "Add new",
  },
  mine: {
    edit: "Edit company details",
  },
  members: {
    title: "Your company's recruiting team",
    inviteHint: "Invite new members to the recruiting team.",
    noteLabel: "Note:",
    note: "Only the owner of the recruiting team (OWNER) can remove members from it.",
    invite: {
      label: "Email address:",
      placeholder: "Enter an email...",
    },
    table: {
      name: "User name",
    },
    notFound: {
      title: "No users found",
      description: "Try other search criteria or add a new user",
    },
    toasts: {
      added: "Member added.",
      removed: "Member removed.",
    },
  },
} as const;

const vi: Translation<typeof en> = {
  fields: {
    name: "Tên công ty",
  },
  logoAlt: "Logo {{name}}",
  dates: {
    created: "Ngày tạo:",
    updated: "Cập nhật:",
  },
  notFound: {
    title: "Không tìm thấy công ty nào",
    description: "Thử thay đổi tiêu chí tìm kiếm hoặc thêm công ty mới",
  },
  errors: {
    loadList: "Không thể lấy danh sách công ty.",
    loadOne: "Không thể lấy thông tin công ty",
    failed: "Thao tác thất bại.",
    deleteFailed: "Xóa công ty thất bại.",
  },
  toasts: {
    created: "Thêm công ty thành công.",
    updated: "Cập nhật công ty thành công.",
    deleted: "Xóa công ty thành công.",
  },
  deleteDialog: {
    title: "Bạn có chắc muốn xóa công ty này?",
    description:
      "Hành động này sẽ <danger>xóa CÔNG TY, CÔNG VIỆC và HỒ SƠ ỨNG VIÊN</danger> đã nộp cho công việc này.",
  },
  search: {
    name: "Tên công ty:",
    namePlaceholder: "nhập tên tìm",
    address: "Địa chỉ công ty:",
    addressPlaceholder: "nhập địa chỉ",
    cities: {
      hcm: { label: "HCM", value: "Hồ Chí Minh" },
      hanoi: { label: "HN", value: "Hà Nội" },
      danang: { label: "ĐN", value: "Đà Nẵng" },
    },
  },
  form: {
    title: "Biểu mẫu thông tin công ty",
    editMode: "Chỉnh sửa công ty",
    createMode: "Thêm công ty mới",
    descriptionPlaceholder: "Nhập mô tả công ty...",
    logoPreview: "Xem trước logo",
    saveChanges: "Lưu thay đổi",
  },
  details: {
    title: "Chi tiết công ty",
    breadcrumb: "Danh sách công ty",
    current: "Công ty {{name}}",
  },
  selection: {
    none: "Chưa chọn công ty",
    change: "Đổi công ty",
    choose: "Chọn công ty",
    description: "Chọn một công ty trong danh sách",
    searchPlaceholder: "Tìm kiếm công ty...",
  },
  jobs: {
    hiring_one: "Công ty đang tuyển <highlight>{{count}} việc làm</highlight>",
    hiring_other: "Công ty đang tuyển <highlight>{{count}} việc làm</highlight>",
    location: "Địa điểm:",
    salary: "Lương:",
    quantity: "Số lượng:",
    level: "Cấp bậc:",
    period: "Thời gian:",
  },
  grid: {
    jobs_one: "{{count}} việc làm",
    jobs_other: "{{count}} việc làm",
  },
  top: {
    title: "Top công ty đang tuyển dụng",
    subtitle: "Khám phá những công ty hàng đầu với môi trường làm việc lý tưởng",
    emptyDescription: "Có thể đã có lỗi xảy ra",
    viewJobs: "Xem việc làm",
  },
  browse: {
    title: "Công ty",
    subtitle: "Khám phá các công ty hàng đầu và cơ hội nghề nghiệp",
    stats: {
      companies: "Công ty",
      jobs: "Việc làm",
      candidates: "Ứng viên",
    },
    found_one: "Tìm thấy {{count}} công ty",
    found_other: "Tìm thấy {{count}} công ty",
  },
  manager: {
    title: "Danh sách Công Ty",
    add: "Thêm mới",
  },
  mine: {
    edit: "Chỉnh sửa thông tin công ty",
  },
  members: {
    title: "Đội ngũ tuyển dụng của công ty bạn",
    inviteHint: "Mời thêm thành viên mới vào đội ngũ tuyển dụng.",
    noteLabel: "Lưu ý:",
    note: "Chỉ chủ sở hữu quản lý tuyển dụng (OWNER) mới có quyền loại bỏ thành viên khỏi đội ngũ tuyển dụng.",
    invite: {
      label: "Nhập tên email:",
      placeholder: "Nhập tên email...",
    },
    table: {
      name: "Tên người dùng",
    },
    notFound: {
      title: "Không tìm thấy người dùng nào",
      description: "Thử thay đổi tiêu chí tìm kiếm hoặc thêm người dùng mới",
    },
    toasts: {
      added: "Thêm thành viên mới thành công",
      removed: "Xóa thành viên thành công",
    },
  },
};

export const messages = { en, vi };
