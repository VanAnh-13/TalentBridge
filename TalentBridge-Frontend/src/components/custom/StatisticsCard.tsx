import { Card, CardContent } from "@/components/ui/card";
import { TrendingUp, TrendingDown } from "lucide-react";
import type { ReactNode } from "react";

interface StatisticsCardProps {
    title: string;
    value: number | string;
    icon: ReactNode;
    percentageChange?: number;
    subtitle?: string;
    variant?: "default" | "highlight";
}

const StatisticsCard = ({
    title,
    value,
    icon,
    percentageChange,
    subtitle,
    variant = "default",
}: StatisticsCardProps) => {
    const isPositive = percentageChange !== undefined && percentageChange >= 0;
    const hasPercentage = percentageChange !== undefined;

    return (
        <Card
            className={`${variant === "highlight" ? "border-blue-200 bg-blue-50/50" : ""}`}
        >
            <CardContent className="pt-6">
                <div className="flex items-center justify-between">
                    <div className="flex-1">
                        <p className="text-sm font-medium text-muted-foreground">{title}</p>
                        <div className="flex items-baseline gap-2 mt-2">
                            <h3 className="text-2xl font-bold">
                                {typeof value === "number" ? value.toLocaleString() : value}
                            </h3>
                            {hasPercentage && (
                                <span
                                    className={`flex items-center text-xs font-medium ${isPositive ? "text-green-600" : "text-red-600"
                                        }`}
                                >
                                    {isPositive ? (
                                        <TrendingUp className="w-3 h-3 mr-1" />
                                    ) : (
                                        <TrendingDown className="w-3 h-3 mr-1" />
                                    )}
                                    {isPositive ? "+" : ""}
                                    {percentageChange.toFixed(1)}% so với tháng trước
                                </span>
                            )}
                        </div>
                        {subtitle && (
                            <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>
                        )}
                    </div>
                    <div
                        className={`p-3 rounded-lg ${variant === "highlight"
                                ? "bg-blue-100 text-blue-600"
                                : "bg-gray-100 text-gray-600"
                            }`}
                    >
                        {icon}
                    </div>
                </div>
            </CardContent>
        </Card>
    );
};

export default StatisticsCard;
