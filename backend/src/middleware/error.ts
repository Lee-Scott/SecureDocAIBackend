import { Request, Response, NextFunction } from 'express';
import { logger } from '../config/logger';
import { env } from '../config/env';

// Error response interface
interface ErrorResponse {
  error: string;
  message: string;
  correlationId: string;
  timestamp: string;
  path: string;
  stack?: string; // Only in development
}

// Custom error class for application errors
export class AppError extends Error {
  public statusCode: number;
  public isOperational: boolean;

  constructor(message: string, statusCode: number = 500, isOperational: boolean = true) {
    super(message);
    this.statusCode = statusCode;
    this.isOperational = isOperational;

    Error.captureStackTrace(this, this.constructor);
  }
}

// Not found middleware (404 handler)
export const notFoundHandler = (req: Request, _res: Response, next: NextFunction): void => {
  const error = new AppError(`Resource not found: ${req.originalUrl}`, 404);
  next(error);
};

// Global error handler
export const globalErrorHandler = (
  error: Error | AppError,
  req: Request,
  res: Response,
  _next: NextFunction
): void => {
  let statusCode = 500;
  let message = 'Internal Server Error';
  let isOperational = false;

  // Handle different types of errors
  if (error instanceof AppError) {
    statusCode = error.statusCode;
    message = error.message;
    isOperational = error.isOperational;
  } else if (error.name === 'ValidationError') {
    statusCode = 400;
    message = 'Validation Error';
  } else if (error.name === 'UnauthorizedError') {
    statusCode = 401;
    message = 'Unauthorized';
  } else if (error.name === 'JsonWebTokenError') {
    statusCode = 401;
    message = 'Invalid token';
  } else if (error.name === 'TokenExpiredError') {
    statusCode = 401;
    message = 'Token expired';
  } else if (error.name === 'CastError') {
    statusCode = 400;
    message = 'Invalid data format';
  } else if (error.name === 'MongoError' || error.name === 'MongoServerError') {
    statusCode = 500;
    message = 'Database error';
  } else if (error.name === 'PrismaClientKnownRequestError') {
    statusCode = 400;
    message = 'Database operation failed';
  }

  // Create error response
  const errorResponse: ErrorResponse = {
    error: statusCode >= 500 ? 'Internal Server Error' : 'Bad Request',
    message: isOperational ? message : 'Something went wrong',
    correlationId: req.correlationId || 'unknown',
    timestamp: new Date().toISOString(),
    path: req.path,
  };

  // Include stack trace in development
  if (env.NODE_ENV === 'development' && error.stack) {
    errorResponse.stack = error.stack;
  }

  // Log error with appropriate level
  const logData = {
    correlationId: req.correlationId,
    error: {
      name: error.name,
      message: error.message,
      stack: error.stack,
    },
    request: {
      method: req.method,
      path: req.path,
      ip: req.ip,
      userAgent: req.get('user-agent'),
    },
    statusCode,
    isOperational,
  };

  if (statusCode >= 500) {
    logger.error(logData, 'Server error occurred');
  } else if (statusCode >= 400) {
    logger.warn(logData, 'Client error occurred');
  }

  // Send error response
  res.status(statusCode).json(errorResponse);
};

// Async error handler wrapper
export const asyncHandler = (fn: Function) => {
  return (req: Request, res: Response, next: NextFunction) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
};

// Rate limit error handler
export const rateLimitErrorHandler = (req: Request, res: Response): void => {
  const errorResponse: ErrorResponse = {
    error: 'Too Many Requests',
    message: 'Rate limit exceeded. Please try again later.',
    correlationId: req.correlationId || 'unknown',
    timestamp: new Date().toISOString(),
    path: req.path,
  };

  logger.warn({
    correlationId: req.correlationId,
    ip: req.ip,
    path: req.path,
    userAgent: req.get('user-agent'),
  }, 'Rate limit exceeded');

  res.status(429).json(errorResponse);
};

// HIPAA compliance error handler for sensitive operations
export const hipaaErrorHandler = (
  _error: Error,
  req: Request,
  res: Response,
  _next: NextFunction
): void => {
  // Log HIPAA-sensitive errors with minimal detail
  logger.error({
    correlationId: req.correlationId,
    error: 'HIPAA_SENSITIVE_OPERATION_FAILED',
    path: req.path,
    method: req.method,
    timestamp: new Date().toISOString(),
  }, 'HIPAA-sensitive operation failed');

  // Return generic error message for HIPAA compliance
  const errorResponse: ErrorResponse = {
    error: 'Operation Failed',
    message: 'The requested operation could not be completed. Please contact support if the issue persists.',
    correlationId: req.correlationId || 'unknown',
    timestamp: new Date().toISOString(),
    path: req.path,
  };

  res.status(500).json(errorResponse);
};

// Health check specific error types
export class HealthCheckError extends AppError {
  public component: string;

  constructor(component: string, message: string) {
    super(`Health check failed for ${component}: ${message}`, 503);
    this.component = component;
  }
}

// Database connection error
export class DatabaseError extends AppError {
  constructor(message: string = 'Database connection failed') {
    super(message, 503);
  }
}

// Authentication errors
export class AuthenticationError extends AppError {
  constructor(message: string = 'Authentication failed') {
    super(message, 401);
  }
}

// Authorization errors
export class AuthorizationError extends AppError {
  constructor(message: string = 'Insufficient permissions') {
    super(message, 403);
  }
}

// Validation errors
export class ValidationError extends AppError {
  public field: string | undefined;

  constructor(message: string, field?: string) {
    super(message, 400);
    this.field = field;
  }
}

// HIPAA compliance errors
export class HIPAAComplianceError extends AppError {
  constructor(message: string = 'HIPAA compliance violation detected') {
    super(message, 403);
  }
}