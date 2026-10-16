import "i18next";

import type { resources } from "./resources";

/** Keys and interpolation values of t() are checked against the English catalogues at compile time. */
declare module "i18next" {
  interface CustomTypeOptions {
    defaultNS: "common";
    resources: (typeof resources)["en"];
  }
}
