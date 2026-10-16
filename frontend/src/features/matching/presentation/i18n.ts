import type { Translation } from "@/shared/presentation/i18n/types";

/** Messages of the matching feature (namespace "matching"). */
const en = {
  tone: {
    excellent: "Excellent match",
    good: "Good match",
    partial: "Partial match",
    poor: "Weak match",
  },
  recommended: {
    title: "Recommended for you",
    subtitle: "Based on the skills you follow.",
    fitTitle: "Share of the required skills you already have",
  },
  ranking: {
    title: "AI candidate ranking",
    selectJob: "Select a job",
    disabled: "AI scoring is not enabled on this system (no API key). Applicants are listed, but without scores.",
    enabled: "Scores are given by the {{model}} model from the CV and the job description. They are only a suggestion: the final decision is yours.",
    noJobs: "No jobs yet",
    noJobsHint: "Post a job first",
    noCandidates: "No applicants yet",
    noCandidatesHint: "This job has not received any applications",
    notScored: "Not scored yet",
    rescore: "Score again",
    score: "Score",
    scoredAt: "Scored {{date}}",
    jobsLoadFailed: "Could not load the jobs",
    candidatesLoadFailed: "Could not load the applicants",
    recomputeQueued: "Scoring requested, the result will show up in a few minutes",
    recomputeFailed: "Could not request scoring",
  },
} as const;

const vi: Translation<typeof en> = {
  tone: {
    excellent: "Rất phù hợp",
    good: "Phù hợp",
    partial: "Phù hợp một phần",
    poor: "Ít phù hợp",
  },
  recommended: {
    title: "Gợi ý cho bạn",
    subtitle: "Dựa trên các kỹ năng bạn đang theo dõi.",
    fitTitle: "Tỉ lệ kỹ năng yêu cầu bạn đã có",
  },
  ranking: {
    title: "Xếp hạng ứng viên bằng AI",
    selectJob: "Chọn công việc",
    disabled: "Chấm điểm AI chưa được bật trên hệ thống này (thiếu khóa API). Danh sách vẫn hiển thị nhưng chưa có điểm.",
    enabled: "Điểm do mô hình {{model}} đánh giá từ CV và mô tả công việc. Đây chỉ là gợi ý, quyết định cuối cùng thuộc về bạn.",
    noJobs: "Chưa có công việc nào",
    noJobsHint: "Hãy đăng tin tuyển dụng trước",
    noCandidates: "Chưa có ứng viên",
    noCandidatesHint: "Công việc này chưa nhận được hồ sơ nào",
    notScored: "Chưa chấm điểm",
    rescore: "Chấm lại",
    score: "Chấm điểm",
    scoredAt: "Chấm lúc {{date}}",
    jobsLoadFailed: "Không thể tải danh sách công việc",
    candidatesLoadFailed: "Không thể tải danh sách ứng viên",
    recomputeQueued: "Đã gửi yêu cầu chấm điểm, kết quả sẽ hiện sau ít phút",
    recomputeFailed: "Không thể yêu cầu chấm điểm",
  },
};

export const messages = { en, vi };
