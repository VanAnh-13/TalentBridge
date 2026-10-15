/** The envelope every backend response is wrapped in. */
export interface ApiResponse<T> {
  message: string;
  errorCode: string;
  data: T;
}
