import { AlertCircle } from "lucide-react";
import { useTranslation } from "react-i18next";

export default function ErrorPage() {
  const { t } = useTranslation();
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4 text-center dark:bg-gray-950">
      <div className="mb-6 rounded-full bg-red-100 p-6 dark:bg-red-900">
        <AlertCircle className="h-12 w-12 text-red-600 dark:text-red-300" />
      </div>
      <h1 className="mb-2 text-4xl font-bold text-gray-900 dark:text-white">
        {t("errorPage.title")}
      </h1>
      <p className="mb-6 max-w-md text-gray-600 dark:text-gray-400">
        {t("errorPage.description")}
      </p>
    </div>
  );
}
