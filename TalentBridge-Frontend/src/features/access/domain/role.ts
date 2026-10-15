export interface RolePermission {
  id: number;
  name: string;
  apiPath: string;
  method: string;
  module: string;
}

export interface Role {
  id: number;
  name: string;
  description: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  permissions: RolePermission[];
}

/** A role as other screens refer to it (a user has one). */
export interface RoleRef {
  id: number;
  name: string;
  description: string;
}

export interface RoleData {
  name: string;
  description: string;
  active: boolean;
  permissions: { id: number }[];
}
