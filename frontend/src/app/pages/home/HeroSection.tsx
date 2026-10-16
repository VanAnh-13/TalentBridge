import { Search, Sparkles } from "lucide-react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const HeroSection = () => {
  const { t } = useTranslation("app");
  return (
    <section className="w-full bg-white py-20">
      <div className="max-w-7xl mx-auto flex flex-col-reverse md:flex-row items-center px-6 space-x-12">
        {/* Left: Text Content */}
        <div className="w-full md:w-1/2 text-center md:text-left space-y-6">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-800 flex items-center justify-center md:justify-start">
            <Sparkles className="h-8 w-8 text-orange-500 mr-3" />
            {t("home.hero.titleLine1")}
            <br /> {t("home.hero.titleLine2")}
          </h1>
          <p className="text-lg text-gray-600 text-justify">{t("home.hero.text")}</p>
          <Link
            to="/jobs"
            className="inline-flex items-center px-6 py-3 bg-orange-500 text-white font-medium rounded-md hover:bg-orange-600 transition"
          >
            <Search className="mr-2 h-5 w-5" />
            {t("home.hero.cta")}
          </Link>
        </div>

        {/* Right: Image */}
        <div className="w-full md:w-1/2 mb-10 md:mb-0 flex justify-center h-1/2">
          <img src="/hero-img.png" alt={t("home.hero.imageAlt")} className="h-full object-contain rounded-lg" />
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
