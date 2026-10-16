import {
  Building2,
  Wrench,
  Briefcase,
  Settings,
  FileText,
  User,
  ShieldCheck,
  KeyRound,
  UserCog,
  BarChart3,
  type LucideIcon,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import type { ParseKeys } from "i18next";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/presentation/ui/card";

type Key = ParseKeys<"app">;

interface Functionality {
  category: Key;
  description: Key;
  icon: LucideIcon;
  color: string;
  iconColor: string;
  features: Key[];
  subItems?: { name: Key; description: Key; icon: LucideIcon }[];
}

const functionalities: Functionality[] = [
  {
    category: "dashboard.companies.title",
    description: "dashboard.companies.description",
    icon: Building2,
    color: "bg-blue-50 border-blue-200",
    iconColor: "text-blue-600",
    features: ["dashboard.companies.features.list", "dashboard.companies.features.edit"],
  },
  {
    category: "dashboard.recruitment.title",
    description: "dashboard.recruitment.description",
    icon: Wrench,
    color: "bg-green-50 border-green-200",
    iconColor: "text-green-600",
    features: [
      "dashboard.recruitment.features.jobs",
      "dashboard.recruitment.features.skills",
      "dashboard.recruitment.features.resumes",
    ],
    subItems: [
      { name: "dashboard.recruitment.sub.jobs.name", description: "dashboard.recruitment.sub.jobs.description", icon: Briefcase },
      { name: "dashboard.recruitment.sub.skills.name", description: "dashboard.recruitment.sub.skills.description", icon: Settings },
      { name: "dashboard.recruitment.sub.resumes.name", description: "dashboard.recruitment.sub.resumes.description", icon: FileText },
    ],
  },
  {
    category: "dashboard.accounts.title",
    description: "dashboard.accounts.description",
    icon: User,
    color: "bg-orange-50 border-orange-200",
    iconColor: "text-orange-600",
    features: [
      "dashboard.accounts.features.list",
      "dashboard.accounts.features.create",
      "dashboard.accounts.features.edit",
      "dashboard.accounts.features.status",
    ],
  },
  {
    category: "dashboard.access.title",
    description: "dashboard.access.description",
    icon: ShieldCheck,
    color: "bg-purple-50 border-purple-200",
    iconColor: "text-purple-600",
    features: [
      "dashboard.access.features.all",
      "dashboard.access.features.users",
      "dashboard.access.features.access",
      "dashboard.access.features.config",
    ],
    subItems: [
      { name: "dashboard.access.sub.permissions.name", description: "dashboard.access.sub.permissions.description", icon: KeyRound },
      { name: "dashboard.access.sub.roles.name", description: "dashboard.access.sub.roles.description", icon: UserCog },
    ],
  },
];

const guideSteps: { title: Key; text: Key; color: string }[] = [
  { title: "dashboard.guide.companies.title", text: "dashboard.guide.companies.text", color: "bg-blue-600" },
  { title: "dashboard.guide.jobs.title", text: "dashboard.guide.jobs.text", color: "bg-green-600" },
  { title: "dashboard.guide.users.title", text: "dashboard.guide.users.text", color: "bg-orange-600" },
  { title: "dashboard.guide.monitor.title", text: "dashboard.guide.monitor.text", color: "bg-purple-600" },
];

export default function DashboardPage() {
  const { t } = useTranslation("app");

  return (
    <div className="space-y-6 p-6">
      {/* Functionalities Overview */}
      <div className="space-y-6">
        <div className="flex items-center gap-2">
          <BarChart3 className="h-5 w-5 text-blue-600" />
          <h2 className="text-xl font-semibold text-gray-900">{t("dashboard.title")}</h2>
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          {functionalities.map((func) => (
            <Card key={func.category} className={`${func.color} transition-all hover:shadow-lg`}>
              <CardHeader>
                <div className="flex items-center gap-3">
                  <div className={`rounded-lg bg-white p-2 ${func.iconColor}`}>
                    <func.icon className="h-5 w-5" />
                  </div>
                  <div>
                    <CardTitle className="text-lg text-gray-900">{t(func.category)}</CardTitle>
                    <CardDescription className="mt-1 text-gray-600">{t(func.description)}</CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Main features */}
                <div className="space-y-2">
                  {func.features.map((feature) => (
                    <div key={feature} className="flex items-start gap-2">
                      <div className="mt-2 h-1.5 w-1.5 flex-shrink-0 rounded-full bg-gray-400" />
                      <span className="text-sm text-gray-700">{t(feature)}</span>
                    </div>
                  ))}
                </div>

                {/* Sub items if available */}
                {func.subItems && (
                  <div className="space-y-2 border-t border-gray-200 pt-2">
                    <h4 className="text-sm font-medium text-gray-800">{t("dashboard.subFeatures")}</h4>
                    <div className="grid grid-cols-1 gap-2">
                      {func.subItems.map((subItem) => (
                        <div key={subItem.name} className="flex items-center gap-2 rounded-md bg-white/50 p-2">
                          <subItem.icon className="h-4 w-4 text-gray-600" />
                          <div>
                            <div className="text-sm font-medium text-gray-800">{t(subItem.name)}</div>
                            <div className="text-xs text-gray-600">{t(subItem.description)}</div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      {/* Getting started */}
      <Card className="border-blue-200 bg-gradient-to-r from-blue-50 to-indigo-50">
        <CardHeader>
          <CardTitle className="text-lg text-gray-900">{t("dashboard.guide.title")}</CardTitle>
          <CardDescription>{t("dashboard.guide.subtitle")}</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
            {guideSteps.map((step, index) => (
              <div key={step.title} className="space-y-2 text-center">
                <div
                  className={`mx-auto flex h-8 w-8 items-center justify-center rounded-full ${step.color} text-sm font-bold text-white`}
                >
                  {index + 1}
                </div>
                <h4 className="font-medium text-gray-800">{t(step.title)}</h4>
                <p className="text-xs text-gray-600">{t(step.text)}</p>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
