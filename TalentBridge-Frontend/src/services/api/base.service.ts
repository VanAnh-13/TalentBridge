/**
 * Base API Service for Microservices Communication
 * This service provides the foundation for all API calls to the microservices
 */

import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import {
  CIRCUIT_BREAKER_CONFIG,
  RATE_LIMIT_CONFIG,
  SERVICE_ERROR_MESSAGES
} from '@/config/microservices.config';

/**
 * Circuit Breaker State
 */
interface CircuitBreakerState {
  failures: number;
  lastFailureTime: number;
  state: 'CLOSED' | 'OPEN' | 'HALF_OPEN';
  successCount: number;
}

/**
 * Rate Limiter State
 */
interface RateLimiterState {
  tokens: number;
  lastRefill: number;
}

/**
 * API Response wrapper
 */
export interface ApiResponse<T = any> {
  statusCode: number;
  message: string;
  data?: T;
  metadata?: Record<string, any>;
  timestamp: string;
  success: boolean;
  errors?: Array<{
    field?: string;
    message: string;
    code?: string;
  }>;
  traceId?: string;
}

/**
 * Pagination metadata
 */
export interface PaginationMeta {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/**
 * Request options
 */
export interface RequestOptions extends AxiosRequestConfig {
  skipAuth?: boolean;
  retryAttempts?: number;
  retryDelay?: number;
  skipCircuitBreaker?: boolean;
  skipRateLimiter?: boolean;
  serviceKey?: string;
}

/**
 * Base API Service Class
 */
export class BaseApiService {
  protected axiosInstance: AxiosInstance;
  private circuitBreakers: Map<string, CircuitBreakerState> = new Map();
  private rateLimiter: RateLimiterState;
  private pendingRequests: Map<string, AbortController> = new Map();

  constructor(baseURL: string, defaultTimeout: number = 15000) {
    // Initialize axios instance
    this.axiosInstance = axios.create({
      baseURL,
      timeout: defaultTimeout,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      withCredentials: true,
    });

    // Initialize rate limiter
    this.rateLimiter = {
      tokens: RATE_LIMIT_CONFIG.burstSize,
      lastRefill: Date.now(),
    };

    // Setup interceptors
    this.setupRequestInterceptor();
    this.setupResponseInterceptor();
  }

  /**
   * Setup request interceptor
   */
  private setupRequestInterceptor(): void {
    this.axiosInstance.interceptors.request.use(
      async (config) => {
        // Add authentication token
        const token = this.getAuthToken();
        if (token && !(config as RequestOptions).skipAuth) {
          config.headers.Authorization = `Bearer ${token}`;
        }

        // Add request ID for tracing
        config.headers['X-Request-ID'] = this.generateRequestId();

        // Add timestamp
        config.headers['X-Request-Timestamp'] = new Date().toISOString();

        // Check rate limiter
        if (!(config as RequestOptions).skipRateLimiter) {
          await this.checkRateLimit();
        }

        // Check circuit breaker
        const serviceKey = (config as RequestOptions).serviceKey || this.getServiceKeyFromUrl(config.url || '');
        if (!(config as RequestOptions).skipCircuitBreaker) {
          this.checkCircuitBreaker(serviceKey);
        }

        // Add abort controller for request cancellation
        const controller = new AbortController();
        config.signal = controller.signal;
        this.pendingRequests.set(config.headers['X-Request-ID'], controller);

        return config;
      },
      (error) => {
        return Promise.reject(this.handleError(error));
      }
    );
  }

  /**
   * Setup response interceptor
   */
  private setupResponseInterceptor(): void {
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        // Remove from pending requests
        const requestId = response.config.headers['X-Request-ID'];
        if (requestId) {
          this.pendingRequests.delete(requestId);
        }

        // Update circuit breaker on success
        const serviceKey = (response.config as RequestOptions).serviceKey ||
                          this.getServiceKeyFromUrl(response.config.url || '');
        this.onRequestSuccess(serviceKey);

        // Handle token refresh
        const newToken = response.headers['x-new-token'];
        if (newToken) {
          this.setAuthToken(newToken);
        }

        // Transform response if needed
        return this.transformResponse(response);
      },
      async (error: AxiosError) => {
        // Remove from pending requests
        const requestId = error.config?.headers['X-Request-ID'];
        if (requestId) {
          this.pendingRequests.delete(requestId);
        }

        // Handle token expiration
        if (error.response?.status === 401 && !error.config?.url?.includes('/auth/refresh')) {
          const refreshed = await this.refreshToken();
          if (refreshed) {
            // Retry original request
            return this.axiosInstance.request(error.config!);
          }
        }

        // Update circuit breaker on failure
        const serviceKey = (error.config as RequestOptions)?.serviceKey ||
                          this.getServiceKeyFromUrl(error.config?.url || '');
        this.onRequestFailure(serviceKey);

        // Implement retry logic
        const options = error.config as RequestOptions;
        if (options?.retryAttempts && options.retryAttempts > 0) {
          return this.retryRequest(error.config!, options.retryAttempts, options.retryDelay);
        }

        return Promise.reject(this.handleError(error));
      }
    );
  }

  /**
   * Check rate limit before making request
   */
  private async checkRateLimit(): Promise<void> {
    const now = Date.now();
    const timePassed = now - this.rateLimiter.lastRefill;
    const tokensToAdd = Math.floor(timePassed / 1000) * RATE_LIMIT_CONFIG.maxRequestsPerSecond;

    this.rateLimiter.tokens = Math.min(
      RATE_LIMIT_CONFIG.burstSize,
      this.rateLimiter.tokens + tokensToAdd
    );
    this.rateLimiter.lastRefill = now;

    if (this.rateLimiter.tokens <= 0) {
      await new Promise(resolve => setTimeout(resolve, 100));
      return this.checkRateLimit();
    }

    this.rateLimiter.tokens--;
  }

  /**
   * Check circuit breaker state
   */
  private checkCircuitBreaker(serviceKey: string): void {
    const breaker = this.circuitBreakers.get(serviceKey);
    if (!breaker) return;

    const now = Date.now();

    switch (breaker.state) {
      case 'OPEN':
        if (now - breaker.lastFailureTime > CIRCUIT_BREAKER_CONFIG.timeout) {
          breaker.state = 'HALF_OPEN';
          breaker.successCount = 0;
        } else {
          throw new Error(`Circuit breaker is OPEN for ${serviceKey}: ${CIRCUIT_BREAKER_CONFIG.fallbackMessage}`);
        }
        break;

      case 'HALF_OPEN':
        // Allow request to proceed for testing
        break;

      case 'CLOSED':
        // Normal operation
        break;
    }
  }

  /**
   * Handle successful request for circuit breaker
   */
  private onRequestSuccess(serviceKey: string): void {
    const breaker = this.circuitBreakers.get(serviceKey);
    if (!breaker) return;

    if (breaker.state === 'HALF_OPEN') {
      breaker.successCount++;
      if (breaker.successCount >= 3) {
        breaker.state = 'CLOSED';
        breaker.failures = 0;
        breaker.successCount = 0;
      }
    } else if (breaker.state === 'CLOSED') {
      breaker.failures = 0;
    }
  }

  /**
   * Handle failed request for circuit breaker
   */
  private onRequestFailure(serviceKey: string): void {
    let breaker = this.circuitBreakers.get(serviceKey);
    if (!breaker) {
      breaker = {
        failures: 0,
        lastFailureTime: 0,
        state: 'CLOSED',
        successCount: 0,
      };
      this.circuitBreakers.set(serviceKey, breaker);
    }

    breaker.failures++;
    breaker.lastFailureTime = Date.now();

    if (breaker.state === 'HALF_OPEN') {
      breaker.state = 'OPEN';
      breaker.successCount = 0;
    } else if (breaker.failures >= CIRCUIT_BREAKER_CONFIG.threshold) {
      breaker.state = 'OPEN';
    }
  }

  /**
   * Retry failed request
   */
  private async retryRequest(
    config: AxiosRequestConfig,
    retries: number,
    delay: number = 1000
  ): Promise<AxiosResponse> {
    for (let i = 0; i < retries; i++) {
      await new Promise(resolve => setTimeout(resolve, delay * Math.pow(2, i)));
      try {
        return await this.axiosInstance.request(config);
      } catch (error) {
        if (i === retries - 1) throw error;
      }
    }
    throw new Error('Max retries reached');
  }

  /**
   * Transform response data
   */
  private transformResponse(response: AxiosResponse<ApiResponse>): AxiosResponse<ApiResponse> {
    // Add pagination metadata if present
    if (response.headers['x-total-count']) {
      response.data.metadata = {
        ...response.data.metadata,
        pagination: {
          totalItems: parseInt(response.headers['x-total-count']),
          currentPage: parseInt(response.headers['x-page-number'] || '1'),
          itemsPerPage: parseInt(response.headers['x-page-size'] || '20'),
          totalPages: parseInt(response.headers['x-total-pages'] || '1'),
        },
      };
    }

    return response;
  }

  /**
   * Handle errors
   */
  private handleError(error: AxiosError | Error): Error {
    if (axios.isAxiosError(error)) {
      const response = error.response?.data as ApiResponse;
      const status = error.response?.status;

      if (error.code === 'ECONNABORTED') {
        return new Error(SERVICE_ERROR_MESSAGES.timeout);
      }

      if (!error.response) {
        return new Error(SERVICE_ERROR_MESSAGES.network);
      }

      if (response?.message) {
        return new Error(response.message);
      }

      switch (status) {
        case 400:
          return new Error('Invalid request. Please check your input.');
        case 401:
          return new Error('Authentication required. Please login.');
        case 403:
          return new Error('You do not have permission to perform this action.');
        case 404:
          return new Error('The requested resource was not found.');
        case 409:
          return new Error('A conflict occurred. The resource may already exist.');
        case 422:
          return new Error('The request could not be processed. Please check your input.');
        case 429:
          return new Error('Too many requests. Please try again later.');
        case 500:
          return new Error('An internal server error occurred.');
        case 502:
          return new Error('Bad gateway. The service is temporarily unavailable.');
        case 503:
          return new Error('Service unavailable. Please try again later.');
        default:
          return new Error(SERVICE_ERROR_MESSAGES.unknown);
      }
    }

    return error as Error;
  }

  /**
   * Get authentication token
   */
  private getAuthToken(): string | null {
    // Try to get from localStorage first
    const token = localStorage.getItem('access_token');
    if (token) return token;

    // Try to get from sessionStorage
    return sessionStorage.getItem('access_token');
  }

  /**
   * Set authentication token
   */
  private setAuthToken(token: string): void {
    const rememberMe = localStorage.getItem('remember_me') === 'true';
    if (rememberMe) {
      localStorage.setItem('access_token', token);
    } else {
      sessionStorage.setItem('access_token', token);
    }
  }

  /**
   * Refresh authentication token
   */
  private async refreshToken(): Promise<boolean> {
    try {
      const refreshToken = localStorage.getItem('refresh_token') ||
                          sessionStorage.getItem('refresh_token');
      if (!refreshToken) return false;

      const response = await this.axiosInstance.post('/auth/refresh', {
        refreshToken,
      });

      const { accessToken, refreshToken: newRefreshToken } = response.data.data;
      this.setAuthToken(accessToken);

      const rememberMe = localStorage.getItem('remember_me') === 'true';
      if (rememberMe) {
        localStorage.setItem('refresh_token', newRefreshToken);
      } else {
        sessionStorage.setItem('refresh_token', newRefreshToken);
      }

      return true;
    } catch {
      // Clear tokens on refresh failure
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      sessionStorage.removeItem('access_token');
      sessionStorage.removeItem('refresh_token');
      return false;
    }
  }

  /**
   * Generate unique request ID
   */
  private generateRequestId(): string {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * Extract service key from URL
   */
  private getServiceKeyFromUrl(url: string): string {
    const match = url.match(/\/api\/v\d+\/([^/]+)/);
    return match ? match[1] : 'default';
  }

  /**
   * Cancel pending request
   */
  public cancelRequest(requestId: string): void {
    const controller = this.pendingRequests.get(requestId);
    if (controller) {
      controller.abort();
      this.pendingRequests.delete(requestId);
    }
  }

  /**
   * Cancel all pending requests
   */
  public cancelAllRequests(): void {
    this.pendingRequests.forEach((controller) => controller.abort());
    this.pendingRequests.clear();
  }

  /**
   * Generic GET request
   */
  public async get<T = any>(url: string, options?: RequestOptions): Promise<ApiResponse<T>> {
    const response = await this.axiosInstance.get<ApiResponse<T>>(url, options);
    return response.data;
  }

  /**
   * Generic POST request
   */
  public async post<T = any>(url: string, data?: any, options?: RequestOptions): Promise<ApiResponse<T>> {
    const response = await this.axiosInstance.post<ApiResponse<T>>(url, data, options);
    return response.data;
  }

  /**
   * Generic PUT request
   */
  public async put<T = any>(url: string, data?: any, options?: RequestOptions): Promise<ApiResponse<T>> {
    const response = await this.axiosInstance.put<ApiResponse<T>>(url, data, options);
    return response.data;
  }

  /**
   * Generic PATCH request
   */
  public async patch<T = any>(url: string, data?: any, options?: RequestOptions): Promise<ApiResponse<T>> {
    const response = await this.axiosInstance.patch<ApiResponse<T>>(url, data, options);
    return response.data;
  }

  /**
   * Generic DELETE request
   */
  public async delete<T = any>(url: string, options?: RequestOptions): Promise<ApiResponse<T>> {
    const response = await this.axiosInstance.delete<ApiResponse<T>>(url, options);
    return response.data;
  }

  /**
   * File upload request
   */
  public async upload<T = any>(
    url: string,
    file: File | Blob,
    fieldName: string = 'file',
    additionalData?: Record<string, any>,
    options?: RequestOptions
  ): Promise<ApiResponse<T>> {
    const formData = new FormData();
    formData.append(fieldName, file);

    if (additionalData) {
      Object.entries(additionalData).forEach(([key, value]) => {
        formData.append(key, value);
      });
    }

    const response = await this.axiosInstance.post<ApiResponse<T>>(url, formData, {
      ...options,
      headers: {
        ...options?.headers,
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  }

  /**
   * Download file request
   */
  public async download(url: string, filename?: string, options?: RequestOptions): Promise<void> {
    const response = await this.axiosInstance.get(url, {
      ...options,
      responseType: 'blob',
    });

    const blob = new Blob([response.data]);
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = filename || 'download';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);
  }

  /**
   * Health check for service
   */
  public async healthCheck(): Promise<boolean> {
    try {
      const response = await this.axiosInstance.get('/health', {
        timeout: 5000,
        skipCircuitBreaker: true,
        skipRateLimiter: true,
        skipAuth: true,
      } as RequestOptions);
      return response.status === 200;
    } catch {
      return false;
    }
  }
}

export default BaseApiService;
