import helmet from 'helmet';
import cors from 'cors';
import rateLimit from 'express-rate-limit';
import hpp from 'hpp';
import { Request, Response, NextFunction } from 'express';
import { env } from '../config/env';
import { logger, generateCorrelationId } from '../config/logger';

// Helmet configuration for security headers
export const helmetConfig = helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      scriptSrc: ["'self'"],
      imgSrc: ["'self'", "data:", "https:"],
      connectSrc: ["'self'"],
      fontSrc: ["'self'"],
      objectSrc: ["'none'"],
      mediaSrc: ["'self'"],
      frameSrc: ["'none'"],
    },
  },
  crossOriginEmbedderPolicy: false, // Allows Swagger UI
  hsts: {
    maxAge: 31536000, // 1 year
    includeSubDomains: true,
    preload: true,
  },
});

// CORS configuration with restrictive settings
export const corsConfig = cors({
  origin: (origin, callback) => {
    // Allow requests with no origin (like mobile apps or curl requests)
    if (!origin) return callback(null, true);
    
    const allowedOrigins = env.CORS_ORIGIN.split(',').map(o => o.trim());
    
    if (allowedOrigins.includes(origin)) {
      return callback(null, true);
    }
    
    logger.warn({ origin }, 'CORS request blocked - origin not allowed');
    return callback(new Error('Not allowed by CORS'), false);
  },
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: [
    'Origin',
    'X-Requested-With',
    'Content-Type',
    'Accept',
    'Authorization',
    'X-Correlation-ID'
  ],
  credentials: true,
  maxAge: 86400, // 24 hours
});

// Rate limiting configuration
export const rateLimitConfig = rateLimit({
  windowMs: env.RATE_LIMIT_WINDOW_MS,
  max: env.RATE_LIMIT_MAX_REQUESTS,
  message: {
    error: 'Too many requests from this IP, please try again later.',
    retryAfter: Math.ceil(env.RATE_LIMIT_WINDOW_MS / 1000),
  },
  standardHeaders: true,
  legacyHeaders: false,
  handler: (req: Request, res: Response) => {
    logger.warn({
      ip: req.ip,
      userAgent: req.get('user-agent'),
      path: req.path,
    }, 'Rate limit exceeded');
    
    res.status(429).json({
      error: 'Too many requests from this IP, please try again later.',
      retryAfter: Math.ceil(env.RATE_LIMIT_WINDOW_MS / 1000),
    });
  },
});

// Stricter rate limiting for sensitive endpoints
export const strictRateLimitConfig = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // 5 requests per window
  message: {
    error: 'Too many attempts for sensitive operation, please try again later.',
    retryAfter: 900, // 15 minutes in seconds
  },
  standardHeaders: true,
  legacyHeaders: false,
});

// HTTP Parameter Pollution protection
export const hppConfig = hpp({
  whitelist: ['sort', 'fields', 'page', 'limit'], // Allow arrays for these parameters
});

// Custom XSS protection middleware (replacing deprecated xss-clean)
export const xssProtection = (req: Request, _res: Response, next: NextFunction): void => {
  const sanitizeValue = (value: any): any => {
    if (typeof value === 'string') {
      // Remove potentially dangerous HTML tags and attributes
      return value
        .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
        .replace(/<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi, '')
        .replace(/javascript:/gi, '')
        .replace(/on\w+\s*=/gi, '')
        .replace(/<[^>]*>/g, ''); // Remove all HTML tags for healthcare data
    }
    
    if (typeof value === 'object' && value !== null) {
      const sanitized: any = Array.isArray(value) ? [] : {};
      for (const [key, val] of Object.entries(value)) {
        sanitized[key] = sanitizeValue(val);
      }
      return sanitized;
    }
    
    return value;
  };

  try {
    // Sanitize request body
    if (req.body && typeof req.body === 'object') {
      req.body = sanitizeValue(req.body);
    }

    // Sanitize query parameters (create new object to avoid read-only issues)
    if (req.query && typeof req.query === 'object') {
      const sanitizedQuery = sanitizeValue(req.query);
      Object.defineProperty(req, 'query', {
        value: sanitizedQuery,
        writable: true,
        configurable: true,
      });
    }

    // Sanitize URL parameters
    if (req.params && typeof req.params === 'object') {
      req.params = sanitizeValue(req.params);
    }
  } catch (error) {
    // If sanitization fails, log error but continue
    console.warn('XSS sanitization failed:', error);
  }

  next();
};

// Request logging middleware with correlation ID
export const requestLogger = (req: Request, res: Response, next: NextFunction) => {
  const correlationId = generateCorrelationId();
  req.correlationId = correlationId;
  
  // Add correlation ID to response headers
  res.setHeader('X-Correlation-ID', correlationId);
  
  const startTime = Date.now();
  
  logger.info({
    correlationId,
    req,
    message: 'Request started'
  });

  // Log response when finished
  res.on('finish', () => {
    const duration = Date.now() - startTime;
    
    logger.info({
      correlationId,
      res,
      duration,
      message: 'Request completed'
    });
  });

  next();
};

// Security headers middleware
export const securityHeaders = (_req: Request, res: Response, next: NextFunction): void => {
  // Remove potentially sensitive headers
  res.removeHeader('X-Powered-By');
  res.removeHeader('Server');
  
  // Add custom security headers
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('X-Frame-Options', 'DENY');
  res.setHeader('X-XSS-Protection', '1; mode=block');
  res.setHeader('Referrer-Policy', 'strict-origin-when-cross-origin');
  res.setHeader('Permissions-Policy', 'geolocation=(), microphone=(), camera=()');
  
  next();
};

// Declare custom properties for Request interface
declare global {
  namespace Express {
    interface Request {
      correlationId?: string;
    }
  }
}