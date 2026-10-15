export interface Company {
  id: number;
  name: string;
  description: string;
  address: string;
  logoUrl?: string;
  createdAt: string;
  updatedAt: string;
  /** Only present in listings that count open jobs. */
  jobsCount?: number;
}

/** What a user fills in to create or change a company. */
export interface CompanyData {
  name: string;
  description: string;
  address: string;
}

/** A recruiter working for a company. */
export interface Member {
  id: number;
  name: string;
  email: string;
  owner: boolean;
}

/** A company as other screens refer to it (a job belongs to one). */
export interface CompanyRef {
  id: number;
  name: string;
  address: string;
  logoUrl?: string;
}
