// TRANSITIONAL: the pages that still call services/*.ts get server errors as ApiError, like the migrated features do.
// Deleted together with the last legacy service.
import axios from "axios";

import { ApiError } from "@/shared/domain/errors";
import { httpClient } from "@/shared/infrastructure/http/httpClient";

httpClient.interceptors.response.use(
  (response) => response,
  (err) => {
    if (!axios.isAxiosError(err)) return Promise.reject(err);
    const body = err.response?.data as { message?: string; errorCode?: string } | undefined;
    return Promise.reject(
      new ApiError(body?.message || err.message, {
        status: err.response?.status,
        errorCode: body?.errorCode,
        serverMessage: body?.message || undefined,
      }),
    );
  },
);

export default httpClient;
