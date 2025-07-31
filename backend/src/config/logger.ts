import pino from 'pino';
import { env } from './env';

// Define PII fields that should be masked in logs
const piiFields = [
  'password',
  'token',
  'authorization',
  'cookie',
  'ssn',
  'social_security_number',
  'email',
  'phone',
  'address',
  'credit_card',
  'account_number',
  'genetic_data',
  'lab_results',
  'health_data'
];

// Custom serializer to mask PII data
const serializer = (obj: any): any => {
  if (obj === null || typeof obj !== 'object') {
    return obj;
  }

  const serialized = Array.isArray(obj) ? [] : {};

  for (const [key, value] of Object.entries(obj)) {
    const keyLower = key.toLowerCase();
    
    // Check if key contains PII field names
    const isPII = piiFields.some(field => keyLower.includes(field));
    
    if (isPII) {
      (serialized as any)[key] = '[MASKED]';
    } else if (value && typeof value === 'object') {
      (serialized as any)[key] = serializer(value);
    } else {
      (serialized as any)[key] = value;
    }
  }

  return serialized;
};

// Create logger configuration
const loggerConfig: pino.LoggerOptions = {
  level: env.LOG_LEVEL,
  timestamp: pino.stdTimeFunctions.isoTime,
  formatters: {
    level: (label) => ({ level: label.toUpperCase() }),
  },
  serializers: {
    req: (req) => serializer({
      method: req.method,
      url: req.url,
      headers: req.headers,
      query: req.query,
      params: req.params,
      ip: req.ip,
      userAgent: req.get('user-agent'),
    }),
    res: (res) => ({
      statusCode: res.statusCode,
      headers: res.getHeaders(),
    }),
    err: pino.stdSerializers.err,
  },
  redact: {
    paths: [
      'req.headers.authorization',
      'req.headers.cookie',
      'req.body.password',
      'req.body.token',
      'req.body.ssn',
      'req.body.email',
      'req.body.phone',
      'req.body.address',
      '*.password',
      '*.token',
      '*.ssn',
      '*.genetic_data',
      '*.lab_results',
      '*.health_data'
    ],
    censor: '[HIPAA_REDACTED]'
  }
};

// Development pretty printing
if (env.NODE_ENV === 'development') {
  loggerConfig.transport = {
    target: 'pino-pretty',
    options: {
      colorize: true,
      ignore: 'pid,hostname',
      translateTime: 'SYS:yyyy-mm-dd HH:MM:ss',
    },
  };
}

export const logger = pino(loggerConfig);

// Request correlation ID middleware
export const generateCorrelationId = (): string => {
  return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
};