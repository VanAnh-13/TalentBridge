import { Globe, Monitor, Smartphone, Tablet } from "lucide-react";

export const getBrowserInfo = (userAgent?: string | null) => {
  if (!userAgent) return "Trình duyệt không xác định";
  switch (true) {
    case /Chrome/.test(userAgent):
      return "Chrome";
    case /Firefox/.test(userAgent):
      return "Firefox";
    case /Safari/.test(userAgent):
      return "Safari";
    case /Edge/.test(userAgent):
      return "Edge";
    case /Opera/.test(userAgent):
      return "Opera";
    default:
      return "Trình duyệt khác";
  }
};

export const getLocationInfo = (userAgent?: string | null) => {
  if (!userAgent) return "Không xác định";
  switch (true) {
    case /Windows/.test(userAgent):
      return "Windows";
    case /Mac/.test(userAgent):
      return "macOS";
    case /Linux/.test(userAgent):
      return "Linux";
    case /Android/.test(userAgent):
      return "Android";
    case /iOS/.test(userAgent):
      return "iOS";
    default:
      return "Không xác định";
  }
};

export const getDeviceIcon = (deviceType?: string | null) => {
  if (!deviceType) return <Globe className="h-5 w-5 text-orange-500" />;

  switch (deviceType.toLowerCase()) {
    case "mobile":
    case "smartphone":
      return <Smartphone className="h-5 w-5 text-orange-500" />;
    case "tablet":
      return <Tablet className="h-5 w-5 text-orange-500" />;
    case "desktop":
    case "computer":
      return <Monitor className="h-5 w-5 text-orange-500" />;
    default:
      return <Globe className="h-5 w-5 text-orange-500" />;
  }
};
