/**
 * Microservices Configuration for TalentBridge Frontend
 * This file contains all the configuration needed to connect to the microservices architecture
 */

export interface MicroserviceConfig {
  baseUrl: string;
  timeout?: number;
  retryAttempts?: number;
  retryDelay?: number;
}

export interface ServiceEndpoints {
  auth: string;
  users: string;
  companies: string;
  jobs: string;
  resumes: string;
  notifications: string;
  skills: string;
  roles: string;
  permissions: string;
  subscribers: string;
  email: string;
}

/**
 * Environment-specific configurations
 */
const environments = {
  development: {
    apiGateway: 'http://localhost:8080',
    directServices: {
      auth: 'http://localhost:8081',
      users: 'http://localhost:8082',
      companies: 'http://localhost:8083',
      jobs: 'http://localhost:8084',
      resumes: 'http://localhost:8085',
      notifications: 'http://localhost:8086',
    },
  },
  docker: {
    apiGateway: 'http://localhost:8080',
    directServices: null, // All traffic through API Gateway in Docker
  },
  staging: {
    apiGateway: 'https://staging-api.talentbridge.com',
    directServices: null,
  },
  production: {
    apiGateway: 'https://api.talentbridge.com',
    directServices: null,
  },
};

/**
 * Get the current environment
 */
const getCurrentEnvironment = (): keyof typeof environments => {
  const env = import.meta.env.VITE_APP_ENVIRONMENT || 'development';
  return env as keyof typeof environments;
};

/**
 * Get the API Gateway URL for the current environment
 */
export const getApiGatewayUrl = (): string => {
  const env = getCurrentEnvironment();
  return import.meta.env.VITE_API_GATEWAY_URL || environments[env].apiGateway;
};

/**
 * Check if direct service calls are enabled (development only)
 */
export const isDirectServiceEnabled = (): boolean => {
  const env = getCurrentEnvironment();
  return env === 'development' && !!environments[env].directServices;
};

/**
 * API Version
 */
export const API_VERSION = 'v1';

/**
 * Build the base URL for API calls
 */
export const buildApiUrl = (path: string): string => {
  const baseUrl = getApiGatewayUrl();
  return `${baseUrl}/api/${API_VERSION}${path}`;
};

/**
 * Service endpoint paths (relative to API Gateway)
 */
export const SERVICE_ENDPOINTS: ServiceEndpoints = {
  auth: '/auth',
  users: '/users',
  companies: '/companies',
  jobs: '/jobs',
  resumes: '/resumes',
  notifications: '/notifications',
  skills: '/skills',
  roles: '/roles',
  permissions: '/permissions',
  subscribers: '/subscribers',
  email: '/email',
};

/**
 * Microservice-specific configurations
 */
export const MICROSERVICE_CONFIG: Record<keyof ServiceEndpoints, MicroserviceConfig> = {
  auth: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.auth),
    timeout: 10000,
    retryAttempts: 2,
    retryDelay: 500,
  },
  users: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.users),
    timeout: 15000,
    retryAttempts: 3,
    retryDelay: 1000,
  },
  companies: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.companies),
    timeout: 15000,
    retryAttempts: 3,
    retryDelay: 1000,
  },
  jobs: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.jobs),
    timeout: 15000,
    retryAttempts: 3,
    retryDelay: 1000,
  },
  resumes: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.resumes),
    timeout: 30000, // Longer timeout for file uploads
    retryAttempts: 2,
    retryDelay: 1000,
  },
  notifications: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.notifications),
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 500,
  },
  skills: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.skills),
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 500,
  },
  roles: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.roles),
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 500,
  },
  permissions: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.permissions),
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 500,
  },
  subscribers: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.subscribers),
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 500,
  },
  email: {
    baseUrl: buildApiUrl(SERVICE_ENDPOINTS.email),
    timeout: 15000,
    retryAttempts: 2,
    retryDelay: 1000,
  },
};

/**
 * WebSocket configuration for real-time features
 */
export const WEBSOCKET_CONFIG = {
  url: import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws',
  reconnectInterval: 5000,
  maxReconnectAttempts: 10,
  heartbeatInterval: 30000,
};

/**
 * Circuit Breaker configuration for frontend resilience
 */
export const CIRCUIT_BREAKER_CONFIG = {
  threshold: 5, // Number of failures before opening circuit
  timeout: 60000, // Time in ms before attempting to close circuit
  fallbackMessage: 'Service temporarily unavailable. Please try again later.',
};

/**
 * Rate limiting configuration
 */
export const RATE_LIMIT_CONFIG = {
  maxRequestsPerSecond: 10,
  burstSize: 20,
};

/**
 * Authentication configuration
 */
export const AUTH_CONFIG = {
  tokenRefreshThreshold: 300000, // 5 minutes before expiry
  sessionTimeout: 1800000, // 30 minutes
  rememberMeDuration: 604800000, // 7 days
};

/**
 * File upload configuration
 */
export const FILE_UPLOAD_CONFIG = {
  maxFileSize: 10485760, // 10MB
  allowedFileTypes: [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'image/jpeg',
    'image/png',
  ],
  allowedImageTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  resumeMaxSize: 5242880, // 5MB for resumes
  imageMaxSize: 2097152, // 2MB for images
};

/**
 * Pagination configuration
 */
export const PAGINATION_CONFIG = {
  defaultPageSize: 20,
  pageSizeOptions: [10, 20, 50, 100],
  maxPageSize: 100,
};

/**
 * Cache configuration
 */
export const CACHE_CONFIG = {
  defaultTTL: 300000, // 5 minutes
  userDataTTL: 600000, // 10 minutes
  staticDataTTL: 3600000, // 1 hour
  searchResultsTTL: 180000, // 3 minutes
};

/**
 * Feature flags for gradual rollout
 */
export const FEATURE_FLAGS = {
  enableWebSocket: import.meta.env.VITE_ENABLE_WEBSOCKET === 'true',
  enableNotifications: import.meta.env.VITE_ENABLE_NOTIFICATIONS === 'true',
  enableFileUpload: import.meta.env.VITE_ENABLE_FILE_UPLOAD !== 'false',
  enableAdvancedSearch: import.meta.env.VITE_ENABLE_ADVANCED_SEARCH === 'true',
  enableAnalytics: import.meta.env.VITE_ENABLE_ANALYTICS === 'true',
  enableCache: import.meta.env.VITE_ENABLE_CACHE !== 'false',
  enableOfflineMode: import.meta.env.VITE_ENABLE_OFFLINE === 'true',
};

/**
 * Health check endpoints for monitoring
 */
export const HEALTH_CHECK_ENDPOINTS = {
  apiGateway: `${getApiGatewayUrl()}/actuator/health`,
  auth: `${getApiGatewayUrl()}/api/${API_VERSION}/auth/health`,
  users: `${getApiGatewayUrl()}/api/${API_VERSION}/users/health`,
  companies: `${getApiGatewayUrl()}/api/${API_VERSION}/companies/health`,
  jobs: `${getApiGatewayUrl()}/api/${API_VERSION}/jobs/health`,
  resumes: `${getApiGatewayUrl()}/api/${API_VERSION}/resumes/health`,
  notifications: `${getApiGatewayUrl()}/api/${API_VERSION}/notifications/health`,
};

/**
 * Error messages for service failures
 */
export const SERVICE_ERROR_MESSAGES: Record<string, string> = {
  auth: 'Authentication service is currently unavailable',
  users: 'User service is currently unavailable',
  companies: 'Company service is currently unavailable',
  jobs: 'Job service is currently unavailable',
  resumes: 'Resume service is currently unavailable',
  notifications: 'Notification service is currently unavailable',
  network: 'Network connection error. Please check your internet connection',
  timeout: 'Request timeout. Please try again',
  unknown: 'An unexpected error occurred. Please try again later',
};

/**
 * Service health status
 */
export interface ServiceHealth {
  service: string;
  status: 'healthy' | 'degraded' | 'unhealthy';
  lastChecked: Date;
  responseTime?: number;
  error?: string;
}

/**
 * Initialize service health monitoring
 */
export const initializeHealthMonitoring = (): void => {
  if (import.meta.env.VITE_ENABLE_HEALTH_CHECK === 'true') {
    console.log('Health monitoring initialized for microservices');
    // Implementation would go here
  }
};

export default {
  getApiGatewayUrl,
  buildApiUrl,
  SERVICE_ENDPOINTS,
  MICROSERVICE_CONFIG,
  WEBSOCKET_CONFIG,
  CIRCUIT_BREAKER_CONFIG,
  RATE_LIMIT_CONFIG,
  AUTH_CONFIG,
  FILE_UPLOAD_CONFIG,
  PAGINATION_CONFIG,
  CACHE_CONFIG,
  FEATURE_FLAGS,
  HEALTH_CHECK_ENDPOINTS,
  SERVICE_ERROR_MESSAGES,
  initializeHealthMonitoring,
};
