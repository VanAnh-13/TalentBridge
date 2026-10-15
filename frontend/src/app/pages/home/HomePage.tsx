import HeroSection from "./HeroSection";
import HowItWorksSection from "./HowItWorksSection";
import SearchBar from "./SearchBar";
import { TopCompaniesSection } from "@/features/company";
import { RecommendedJobsSection } from "@/features/matching";

const HomePage = () => {
  return (
    <div>
      <SearchBar />
      <HeroSection />
      <RecommendedJobsSection />
      <HowItWorksSection />
      <TopCompaniesSection />
    </div>
  );
};

export default HomePage;
