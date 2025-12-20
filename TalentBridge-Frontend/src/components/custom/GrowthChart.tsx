import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { MonthlyTrend } from "@/types/statistics";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from "recharts";

interface GrowthChartProps {
    data: MonthlyTrend[];
    title: string;
}

const GrowthChart = ({ data, title }: GrowthChartProps) => {
    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-base font-medium">{title}</CardTitle>
                <p className="text-sm text-muted-foreground">
                    Thống kê số lượng người dùng, việc làm và hồ sơ theo tháng
                </p>
            </CardHeader>
            <CardContent>
                <div className="h-[300px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart
                            data={data}
                            margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                        >
                            <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                            <XAxis
                                dataKey="month"
                                className="text-xs"
                                tick={{ fill: "hsl(var(--muted-foreground))" }}
                            />
                            <YAxis
                                className="text-xs"
                                tick={{ fill: "hsl(var(--muted-foreground))" }}
                            />
                            <Tooltip
                                contentStyle={{
                                    backgroundColor: "hsl(var(--background))",
                                    border: "1px solid hsl(var(--border))",
                                    borderRadius: "8px",
                                }}
                                labelStyle={{ color: "hsl(var(--foreground))" }}
                            />
                            <Legend />
                            <Line
                                type="monotone"
                                dataKey="users"
                                name="Người dùng"
                                stroke="#3b82f6"
                                strokeWidth={2}
                                dot={{ fill: "#3b82f6", strokeWidth: 2 }}
                                activeDot={{ r: 6 }}
                            />
                            <Line
                                type="monotone"
                                dataKey="jobs"
                                name="Việc làm"
                                stroke="#10b981"
                                strokeWidth={2}
                                dot={{ fill: "#10b981", strokeWidth: 2 }}
                                activeDot={{ r: 6 }}
                            />
                            <Line
                                type="monotone"
                                dataKey="resumes"
                                name="Hồ sơ"
                                stroke="#f59e0b"
                                strokeWidth={2}
                                dot={{ fill: "#f59e0b", strokeWidth: 2 }}
                                activeDot={{ r: 6 }}
                            />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </CardContent>
        </Card>
    );
};

export default GrowthChart;
