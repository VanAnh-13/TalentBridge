export interface OverviewStatistics {
    totalUsers: number;
    usersGrowth: number;
    totalCompanies: number;
    companiesGrowth: number;
    activeJobs: number;
    jobsGrowth: number;
    totalResumes: number;
    resumesGrowth: number;
}

export interface MonthlyStatistics {
    newUsers: number;
    totalUsers: number;
    newJobs: number;
    totalJobs: number;
    newResumes: number;
    totalResumes: number;
    subscribers: number;
    resumeAcceptanceRate: number;
}

export interface MonthlyTrend {
    month: string;
    users: number;
    jobs: number;
    resumes: number;
}

export interface DashboardStatistics {
    overview: OverviewStatistics;
    monthly: MonthlyStatistics;
    trends: MonthlyTrend[];
}
