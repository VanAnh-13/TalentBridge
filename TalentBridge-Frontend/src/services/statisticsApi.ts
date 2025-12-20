import axiosClient from "@/lib/axiosClient";
import type { ApiResponse } from "@/types/apiResponse";
import type { DashboardStatistics } from "@/types/statistics";

export const getDashboardStatistics = () => {
    return axiosClient.get<ApiResponse<DashboardStatistics>>("/statistics");
};
