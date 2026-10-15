/** A request the server answered with an error, or could not be delivered at all. */
export class ApiError extends Error {
  /** HTTP status, absent when the request never got an answer (network failure). */
  readonly status?: number;
  /** The application error code from the response body, e.g. UNAUTHORIZED or ACCESS_DENIED. */
  readonly errorCode?: string;
  /** The message the server put in the response body, already meant for the user. */
  readonly serverMessage?: string;

  constructor(
    message: string,
    details: { status?: number; errorCode?: string; serverMessage?: string } = {},
  ) {
    super(message);
    this.name = "ApiError";
    this.status = details.status;
    this.errorCode = details.errorCode;
    this.serverMessage = details.serverMessage;
  }
}

/** The text to show for a failure: the server's own message when it sent one, otherwise the fallback. */
export const errorMessage = (err: unknown, fallback: string): string =>
  (err instanceof ApiError && err.serverMessage) || fallback;
