// ====================
// METHOD COLOR MAPPER
// ====================
const METHOD_COLORS: Record<string, string> = {
  GET: "bg-green-500",
  POST: "bg-blue-500",
  PUT: "bg-yellow-500",
  DELETE: "bg-red-500",
};

export const getMethodColor = (method: string): string => {
  const color = METHOD_COLORS[method.toUpperCase()];

  return `${color ? color : "bg-gray-500"} w-[80px]`;
};

// ====================
// LEVEL COLOR MAPPER (labels: common:levels.<LEVEL>)
// ====================
export const levelColors = {
  INTERN: "bg-blue-100 text-blue-700",
  FRESHER: "bg-green-100 text-green-700",
  MIDDLE: "bg-yellow-100 text-yellow-700",
  SENIOR: "bg-purple-100 text-purple-700",
};

// ====================
// RESUME STATUS COLOR MAPPER (labels: common:resumeStatus.<STATUS>)
// ====================
export const statusOptions = [
  { value: "PENDING", color: "bg-gray-200 text-gray-700" },
  { value: "REVIEWING", color: "bg-blue-100 text-blue-700" },
  { value: "APPROVED", color: "bg-green-100 text-green-700" },
  { value: "REJECTED", color: "bg-red-100 text-red-700" },
] as const;

export const getResumeStatusColor = (status: string) => {
  const statusItem = statusOptions.find((opt) => opt.value === status);
  return statusItem ? statusItem.color : "bg-gray-200 text-gray-700";
};
