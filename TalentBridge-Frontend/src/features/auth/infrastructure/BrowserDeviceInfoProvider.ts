import { UAParser } from "ua-parser-js";

import type { DeviceInfo } from "../domain/account";
import type { DeviceInfoProvider } from "../application/ports";

export class BrowserDeviceInfoProvider implements DeviceInfoProvider {
  current(): DeviceInfo {
    const result = new UAParser().getResult();

    const deviceName =
      result.device.vendor && result.device.model
        ? `${result.device.vendor} ${result.device.model}`
        : `${result.browser.name || "Browser"} on ${result.os.name || "Unknown OS"}`;

    return {
      deviceName,
      deviceType: result.device.type || "desktop",
      userAgent: navigator.userAgent,
    };
  }
}
