import { Request, Response, NextFunction } from 'express';
import { body, query, param, validationResult } from 'express-validator';
import { z } from 'zod';
import { logger } from '../config/logger';

// Error response interface
interface ValidationError {
  field: string;
  message: string;
  value?: any;
}

interface ValidationErrorResponse {
  error: 'Validation failed';
  details: ValidationError[];
  correlationId: string;
}

// Middleware to handle validation errors
export const handleValidationErrors = (req: Request, res: Response, next: NextFunction): void => {
  const errors = validationResult(req);
  
  if (!errors.isEmpty()) {
    const validationErrors: ValidationError[] = errors.array().map(error => ({
      field: error.type === 'field' ? error.path : 'unknown',
      message: error.msg,
      value: error.type === 'field' ? error.value : undefined,
    }));

    const response: ValidationErrorResponse = {
      error: 'Validation failed',
      details: validationErrors,
      correlationId: req.correlationId || 'unknown',
    };

    logger.warn({
      correlationId: req.correlationId,
      validationErrors,
      path: req.path,
      method: req.method,
    }, 'Validation failed');

    res.status(400).json(response);
    return;
  }

  next();
};

// Common validation rules for healthcare data
export const commonValidations = {
  // Email validation with healthcare-specific requirements
  email: body('email')
    .isEmail()
    .withMessage('Must be a valid email address')
    .normalizeEmail()
    .isLength({ max: 254 })
    .withMessage('Email address too long'),

  // Password validation for strong security
  password: body('password')
    .isLength({ min: 12, max: 128 })
    .withMessage('Password must be between 12 and 128 characters')
    .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/)
    .withMessage('Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character'),

  // Phone number validation
  phone: body('phone')
    .isMobilePhone('any')
    .withMessage('Must be a valid phone number')
    .isLength({ max: 20 })
    .withMessage('Phone number too long'),

  // Date validation
  dateOfBirth: body('dateOfBirth')
    .isISO8601()
    .withMessage('Must be a valid date in ISO format')
    .custom((value) => {
      const date = new Date(value);
      const now = new Date();
      const age = now.getFullYear() - date.getFullYear();
      
      if (age < 13 || age > 120) {
        throw new Error('Age must be between 13 and 120 years');
      }
      
      return true;
    }),

  // ID validation (UUID)
  id: param('id')
    .isUUID()
    .withMessage('Must be a valid UUID'),

  // Pagination validation
  page: query('page')
    .optional()
    .isInt({ min: 1, max: 1000 })
    .withMessage('Page must be an integer between 1 and 1000'),

  limit: query('limit')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Limit must be an integer between 1 and 100'),

  // Search query validation
  search: query('search')
    .optional()
    .isLength({ min: 1, max: 100 })
    .withMessage('Search query must be between 1 and 100 characters')
    .trim()
    .escape(),
};

// Zod schemas for complex validation
export const zodSchemas = {
  // User registration schema
  userRegistration: z.object({
    email: z.string().email('Invalid email format').max(254),
    password: z.string()
      .min(12, 'Password must be at least 12 characters')
      .max(128, 'Password must not exceed 128 characters')
      .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
        'Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character'
      ),
    firstName: z.string().min(1).max(50).trim(),
    lastName: z.string().min(1).max(50).trim(),
    dateOfBirth: z.string().datetime().refine((date) => {
      const birthDate = new Date(date);
      const now = new Date();
      const age = now.getFullYear() - birthDate.getFullYear();
      return age >= 13 && age <= 120;
    }, 'Age must be between 13 and 120 years'),
    phone: z.string().max(20).optional(),
    agreeToTerms: z.boolean().refine(val => val === true, 'Must agree to terms'),
    agreeToPrivacy: z.boolean().refine(val => val === true, 'Must agree to privacy policy'),
  }),

  // User login schema
  userLogin: z.object({
    email: z.string().email('Invalid email format'),
    password: z.string().min(1, 'Password is required'),
    rememberMe: z.boolean().optional(),
  }),

  // Health data schema (for future use)
  healthData: z.object({
    type: z.enum(['lab_result', 'genetic_test', 'supplement_order', 'health_metric']),
    data: z.record(z.string(), z.unknown()),
    timestamp: z.string().datetime(),
    confidentialityLevel: z.enum(['public', 'private', 'restricted']).default('private'),
  }),
};

// Zod validation middleware factory
export const validateZodSchema = (schema: z.ZodSchema) => {
  return (req: Request, res: Response, next: NextFunction): void => {
    try {
      const validatedData = schema.parse(req.body);
      req.body = validatedData;
      next();
    } catch (error) {
      if (error instanceof z.ZodError) {
        const validationErrors: ValidationError[] = error.issues.map(err => ({
          field: err.path.join('.'),
          message: err.message,
          value: err.code === 'invalid_type' ? undefined : 
            (err.path[0] !== undefined ? req.body[err.path[0] as keyof typeof req.body] : undefined),
        }));

        const response: ValidationErrorResponse = {
          error: 'Validation failed',
          details: validationErrors,
          correlationId: req.correlationId || 'unknown',
        };

        logger.warn({
          correlationId: req.correlationId,
          validationErrors,
          path: req.path,
          method: req.method,
        }, 'Zod validation failed');

        res.status(400).json(response);
        return;
      }
      
      next(error);
    }
  };
};

// Sanitization middleware for healthcare data
export const sanitizeHealthData = (req: Request, _res: Response, next: NextFunction): void => {
  const sensitiveFields = [
    'ssn', 'social_security_number', 'tax_id',
    'genetic_data', 'dna_sequence', 'lab_results',
    'medical_history', 'diagnosis', 'medication',
    'insurance_id', 'policy_number'
  ];

  const sanitizeObject = (obj: any): any => {
    if (typeof obj !== 'object' || obj === null) {
      return obj;
    }

    const sanitized: any = Array.isArray(obj) ? [] : {};

    for (const [key, value] of Object.entries(obj)) {
      const keyLower = key.toLowerCase();
      
      // Mark sensitive fields for special handling
      if (sensitiveFields.some(field => keyLower.includes(field))) {
        // Log access to sensitive data
        logger.info({
          correlationId: req.correlationId,
          sensitiveField: key,
          path: req.path,
          method: req.method,
        }, 'Sensitive health data accessed');
      }

      if (typeof value === 'object' && value !== null) {
        sanitized[key] = sanitizeObject(value);
      } else {
        sanitized[key] = value;
      }
    }

    return sanitized;
  };

  if (req.body) {
    req.body = sanitizeObject(req.body);
  }

  next();
};