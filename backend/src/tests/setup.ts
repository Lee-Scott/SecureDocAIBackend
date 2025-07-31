import { jest } from '@jest/globals';

// Set test environment variables
process.env.NODE_ENV = 'test';
process.env.PORT = '3001';
process.env.DATABASE_URL = 'postgresql://test:test@localhost:5432/put_on_your_genes_test';
process.env.JWT_SECRET = 'test-jwt-secret-key-minimum-32-characters-long-for-testing';
process.env.SESSION_SECRET = 'test-session-secret-key-minimum-32-characters-long-for-testing';
process.env.CORS_ORIGIN = 'http://localhost:3000';
process.env.RATE_LIMIT_WINDOW_MS = '900000';
process.env.RATE_LIMIT_MAX_REQUESTS = '1000'; // Higher limit for tests
process.env.LOG_LEVEL = 'error'; // Reduce log noise in tests
process.env.BCRYPT_ROUNDS = '4'; // Faster for tests
process.env.SWAGGER_ENABLED = 'false';

// Global test timeout
jest.setTimeout(10000);

// Mock console.log, console.warn, console.error to reduce noise in tests
global.console = {
  ...console,
  log: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
};

// Add custom matchers or global test utilities here
declare global {
  namespace jest {
    interface Matchers<R> {
      toBeValidUUID(): R;
      toBeValidEmail(): R;
      toBeValidPhoneNumber(): R;
    }
  }
}

// Custom Jest matchers
expect.extend({
  toBeValidUUID(received: string) {
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    const pass = uuidRegex.test(received);
    
    if (pass) {
      return {
        message: () => `expected ${received} not to be a valid UUID`,
        pass: true,
      };
    } else {
      return {
        message: () => `expected ${received} to be a valid UUID`,
        pass: false,
      };
    }
  },
  
  toBeValidEmail(received: string) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const pass = emailRegex.test(received);
    
    if (pass) {
      return {
        message: () => `expected ${received} not to be a valid email`,
        pass: true,
      };
    } else {
      return {
        message: () => `expected ${received} to be a valid email`,
        pass: false,
      };
    }
  },
  
  toBeValidPhoneNumber(received: string) {
    // Basic phone number validation
    const phoneRegex = /^\+?[1-9]\d{1,14}$/;
    const pass = phoneRegex.test(received.replace(/[\s\-\(\)]/g, ''));
    
    if (pass) {
      return {
        message: () => `expected ${received} not to be a valid phone number`,
        pass: true,
      };
    } else {
      return {
        message: () => `expected ${received} to be a valid phone number`,
        pass: false,
      };
    }
  },
});

// Global test helpers
export const createMockRequest = (overrides: any = {}): any => ({
  correlationId: 'test-correlation-id',
  ip: '127.0.0.1',
  method: 'GET',
  path: '/test',
  get: jest.fn((header: string) => {
    if (header === 'user-agent') return 'test-user-agent';
    return undefined;
  }),
  ...overrides,
});

export const createMockResponse = (): any => {
  const res: any = {
    statusCode: 200,
    status: jest.fn().mockReturnThis(),
    json: jest.fn().mockReturnThis(),
    send: jest.fn().mockReturnThis(),
    setHeader: jest.fn().mockReturnThis(),
    removeHeader: jest.fn().mockReturnThis(),
    getHeaders: jest.fn().mockReturnValue({}),
  };
  
  // Mock status function to actually set the status code
  res.status.mockImplementation((code: number) => {
    res.statusCode = code;
    return res;
  });
  
  return res;
};

export const createMockNext = (): jest.Mock => jest.fn();