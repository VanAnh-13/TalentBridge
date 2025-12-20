import { useEffect, useState } from "react";
import { getDashboardStatistics } from "@/services/statisticsApi";
import type { DashboardStatistics } from "@/types/statistics";
import StatisticsCard from "@/components/custom/StatisticsCard";
import GrowthChart from "@/components/custom/GrowthChart";
import LoadingSpinner from "@/components/custom/LoadingSpinner";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import {
    Users,
    Building2,
    Briefcase,
    FileText,
    UserPlus,
    FilePlus,
    Mail,
    RefreshCw,
    BarChart3,
    AlertCircle,
} from "lucide-react";

const DashboardPage = () => {
    const [statistics, setStatistics] = useState<DashboardStatistics | null>(
        null,
    );
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [refreshing, setRefreshing] = useState(false);

    const fetchStatistics = async (isRefresh = false) => {
        try {
            if (isRefresh) {
                setRefreshing(true);
            } else {
                setLoading(true);
            }
            setError(null);

            const response = await getDashboardStatistics();
            setStatistics(response.data.data);
        } catch (err) {
            setError("Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.");
            console.error("Failed to fetch statistics:", err);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useEffect(() => {
        fetchStatistics();
    }, []);

    const handleRefresh = () => {
        fetchStatistics(true);
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center h-96">
                <LoadingSpinner className="w-10 h-10" />
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <BarChart3 className="w-6 h-6 text-blue-600" />
                    <h1 className="text-2xl font-bold">Thống kê & Báo cáo</h1>
                </div>
                <Button
                    onClick={handleRefresh}
                    disabled={refreshing}
                    variant="default"
                    className="gap-2"
                >
                    <RefreshCw className={`w-4 h-4 ${refreshing ? "animate-spin" : ""}`} />
                    Làm mới
                </Button>
            </div>

            {/* Error Alert */}
            {error && (
                <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{error}</AlertDescription>
                </Alert>
            )}

            {statistics && (
                <>
                    {/* Overview Statistics */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <StatisticsCard
                            title="Tổng số người dùng"
                            value={statistics.overview.totalUsers}
                            icon={<Users className="w-5 h-5" />}
                            percentageChange={statistics.overview.usersGrowth}
                        />
                        <StatisticsCard
                            title="Tổng số công ty"
                            value={statistics.overview.totalCompanies}
                            icon={<Building2 className="w-5 h-5" />}
                            percentageChange={statistics.overview.companiesGrowth}
                        />
                        <StatisticsCard
                            title="Việc làm đang tuyển"
                            value={statistics.overview.activeJobs}
                            icon={<Briefcase className="w-5 h-5" />}
                            percentageChange={statistics.overview.jobsGrowth}
                            variant="highlight"
                        />
                        <StatisticsCard
                            title="Hồ sơ ứng tuyển"
                            value={statistics.overview.totalResumes}
                            icon={<FileText className="w-5 h-5" />}
                            percentageChange={statistics.overview.resumesGrowth}
                        />
                    </div>

                    {/* Monthly Statistics */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <StatisticsCard
                            title="Người dùng mới (tháng này)"
                            value={statistics.monthly.newUsers}
                            icon={<UserPlus className="w-5 h-5" />}
                            subtitle={`Tổng: ${statistics.monthly.totalUsers.toLocaleString()} người dùng`}
                        />
                        <StatisticsCard
                            title="Việc làm mới (tháng này)"
                            value={statistics.monthly.newJobs}
                            icon={<Briefcase className="w-5 h-5" />}
                            subtitle={`Tổng: ${statistics.monthly.totalJobs.toLocaleString()} việc làm`}
                        />
                        <StatisticsCard
                            title="Hồ sơ mới (tháng này)"
                            value={statistics.monthly.newResumes}
                            icon={<FilePlus className="w-5 h-5" />}
                            subtitle={`Tỷ lệ chấp nhận: ${statistics.monthly.resumeAcceptanceRate.toFixed(1)}%`}
                        />
                        <StatisticsCard
                            title="Người đăng ký"
                            value={statistics.monthly.subscribers}
                            icon={<Mail className="w-5 h-5" />}
                            subtitle="Đăng ký nhận thông báo"
                            variant="highlight"
                        />
                    </div>

                    {/* Growth Chart */}
                    <GrowthChart
                        data={statistics.trends}
                        title="Tăng trưởng theo tháng (6 tháng gần nhất)"
                    />
                </>
            )}
        </div>
    );
};

export default DashboardPage;
