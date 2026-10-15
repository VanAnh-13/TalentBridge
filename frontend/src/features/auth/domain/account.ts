/** The signed-in person as the session knows them. */
export interface Account {
  id: string;
  name: string;
  email: string;
  permissions: string[];
  role: string;
  companyId: string;
  logoUrl: string;
  updatedAt: string;
}

export const ANONYMOUS: Account = {
  id: "",
  name: "",
  email: "",
  logoUrl: "",
  permissions: [],
  role: "",
  companyId: "",
  updatedAt: "",
};

/** The profile of the signed-in person, as shown on their own profile page. */
export interface AccountDetails {
  id: number;
  name: string;
  email: string;
  dob: string;
  address: string;
  gender: "MALE" | "FEMALE" | "OTHER";
  logoUrl: string;
  createdAt: string;
  updatedAt: string;
}

/** A started session: the short-lived access token plus who it belongs to. */
export interface AuthSession {
  accessToken: string;
  user: Account;
}

export interface Credentials {
  email: string;
  password: string;
}

export interface Registration {
  name: string;
  email: string;
  password: string;
  dob: string;
  address: string;
  gender: "MALE" | "FEMALE" | "OTHER";
  recruiter: boolean;
}

/** Which device a session was opened from. */
export interface DeviceInfo {
  deviceName: string;
  deviceType: string;
  userAgent: string;
}

/** A session of the signed-in person, listed so it can be ended remotely. */
export interface DeviceSession extends DeviceInfo {
  sessionId: string;
  loginAt: string;
  current: boolean;
}
