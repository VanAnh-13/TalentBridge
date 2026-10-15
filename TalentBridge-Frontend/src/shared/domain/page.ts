/** One page of a larger result set. */
export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/** What a list screen asks for: which page, how big, and an optional filter expression. */
export interface PageQuery {
  page: number;
  size: number;
  filter: string | null;
}
