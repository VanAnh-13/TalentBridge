/** The right to call one endpoint ("METHOD /path") or to open one area of the UI. */
export interface Permission {
  id: number;
  name: string;
  apiPath: string;
  method: string;
  module: string;
  createdAt: string;
  updatedAt: string;
}

export interface PermissionData {
  name: string;
  apiPath: string;
  method: string;
  module: string;
}
