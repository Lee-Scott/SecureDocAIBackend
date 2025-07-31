import swaggerJsdoc from 'swagger-jsdoc';
import swaggerUi from 'swagger-ui-express';
import { env } from './env';

// Swagger configuration
const swaggerOptions: swaggerJsdoc.Options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Put On Your Genes API',
      version: '1.0.0',
      description: 'HIPAA-compliant healthcare platform API for genetics, supplements, and lab tests',
      contact: {
        name: 'Put On Your Genes Support',
        email: 'support@putonyourgenes.com',
      },
      license: {
        name: 'MIT',
        url: 'https://opensource.org/licenses/MIT',
      },
    },
    servers: [
      {
        url: env.NODE_ENV === 'production' 
          ? 'https://api.putonyourgenes.com' 
          : `http://localhost:${env.PORT}`,
        description: env.NODE_ENV === 'production' ? 'Production server' : 'Development server',
      },
    ],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
          description: 'JWT token for authenticated requests',
        },
        cookieAuth: {
          type: 'apiKey',
          in: 'cookie',
          name: 'session',
          description: 'Session cookie for authenticated requests',
        },
      },
      schemas: {
        Error: {
          type: 'object',
          properties: {
            error: {
              type: 'string',
              description: 'Error type',
            },
            message: {
              type: 'string',
              description: 'Error message',
            },
            correlationId: {
              type: 'string',
              description: 'Request correlation ID for debugging',
            },
            timestamp: {
              type: 'string',
              format: 'date-time',
              description: 'Error timestamp',
            },
            path: {
              type: 'string',
              description: 'Request path where error occurred',
            },
          },
        },
        ValidationError: {
          type: 'object',
          properties: {
            error: {
              type: 'string',
              enum: ['Validation failed'],
            },
            details: {
              type: 'array',
              items: {
                type: 'object',
                properties: {
                  field: {
                    type: 'string',
                    description: 'Field that failed validation',
                  },
                  message: {
                    type: 'string',
                    description: 'Validation error message',
                  },
                  value: {
                    description: 'Invalid value (if safe to expose)',
                  },
                },
              },
            },
            correlationId: {
              type: 'string',
              description: 'Request correlation ID',
            },
          },
        },
        HealthCheck: {
          type: 'object',
          properties: {
            status: {
              type: 'string',
              enum: ['healthy', 'unhealthy'],
            },
            timestamp: {
              type: 'string',
              format: 'date-time',
            },
            uptime: {
              type: 'number',
              description: 'Application uptime in seconds',
            },
            version: {
              type: 'string',
              description: 'Application version',
            },
            environment: {
              type: 'string',
              description: 'Environment name',
            },
            correlationId: {
              type: 'string',
              description: 'Request correlation ID',
            },
            checks: {
              type: 'object',
              properties: {
                database: {
                  type: 'object',
                  properties: {
                    status: {
                      type: 'string',
                      enum: ['healthy', 'unhealthy'],
                    },
                    responseTime: {
                      type: 'number',
                      description: 'Database response time in milliseconds',
                    },
                  },
                },
                memory: {
                  type: 'object',
                  properties: {
                    status: {
                      type: 'string',
                      enum: ['healthy', 'unhealthy'],
                    },
                    usage: {
                      type: 'object',
                      properties: {
                        rss: {
                          type: 'number',
                          description: 'Resident Set Size in bytes',
                        },
                        heapTotal: {
                          type: 'number',
                          description: 'Total heap size in bytes',
                        },
                        heapUsed: {
                          type: 'number',
                          description: 'Used heap size in bytes',
                        },
                        external: {
                          type: 'number',
                          description: 'External memory usage in bytes',
                        },
                      },
                    },
                  },
                },
              },
            },
          },
        },
        User: {
          type: 'object',
          properties: {
            id: {
              type: 'string',
              format: 'uuid',
              description: 'User unique identifier',
            },
            email: {
              type: 'string',
              format: 'email',
              description: 'User email address',
            },
            firstName: {
              type: 'string',
              description: 'User first name',
            },
            lastName: {
              type: 'string',
              description: 'User last name',
            },
            dateOfBirth: {
              type: 'string',
              format: 'date',
              description: 'User date of birth (HIPAA protected)',
            },
            phone: {
              type: 'string',
              description: 'User phone number (HIPAA protected)',
            },
            createdAt: {
              type: 'string',
              format: 'date-time',
              description: 'Account creation timestamp',
            },
            updatedAt: {
              type: 'string',
              format: 'date-time',
              description: 'Last update timestamp',
            },
          },
        },
        UserRegistration: {
          type: 'object',
          required: ['email', 'password', 'firstName', 'lastName', 'dateOfBirth', 'agreeToTerms', 'agreeToPrivacy'],
          properties: {
            email: {
              type: 'string',
              format: 'email',
              description: 'User email address',
            },
            password: {
              type: 'string',
              minLength: 12,
              maxLength: 128,
              description: 'Strong password with mixed case, numbers, and symbols',
            },
            firstName: {
              type: 'string',
              minLength: 1,
              maxLength: 50,
              description: 'User first name',
            },
            lastName: {
              type: 'string',
              minLength: 1,
              maxLength: 50,
              description: 'User last name',
            },
            dateOfBirth: {
              type: 'string',
              format: 'date-time',
              description: 'User date of birth (must be 13+ years old)',
            },
            phone: {
              type: 'string',
              maxLength: 20,
              description: 'User phone number (optional)',
            },
            agreeToTerms: {
              type: 'boolean',
              description: 'Must be true to register',
            },
            agreeToPrivacy: {
              type: 'boolean',
              description: 'Must be true to register (HIPAA compliance)',
            },
          },
        },
        UserLogin: {
          type: 'object',
          required: ['email', 'password'],
          properties: {
            email: {
              type: 'string',
              format: 'email',
              description: 'User email address',
            },
            password: {
              type: 'string',
              description: 'User password',
            },
            rememberMe: {
              type: 'boolean',
              description: 'Remember user session for longer duration',
            },
          },
        },
      },
    },
    security: [
      {
        bearerAuth: [],
      },
    ],
    tags: [
      {
        name: 'Health',
        description: 'System health and monitoring endpoints',
      },
      {
        name: 'Auth',
        description: 'Authentication and authorization endpoints',
      },
      {
        name: 'Users',
        description: 'User management endpoints (HIPAA protected)',
      },
      {
        name: 'Genetics',
        description: 'Genetic testing and analysis endpoints (HIPAA protected)',
      },
      {
        name: 'Lab Tests',
        description: 'Laboratory testing endpoints (HIPAA protected)',
      },
      {
        name: 'Supplements',
        description: 'Supplement ordering and management endpoints',
      },
    ],
  },
  apis: ['./src/routes/*.ts', './src/controllers/*.ts'], // Paths to files containing OpenAPI definitions
};

// Generate Swagger specification
export const swaggerSpec = swaggerJsdoc(swaggerOptions);

// Swagger UI options
export const swaggerUiOptions = {
  explorer: true,
  customCss: `
    .swagger-ui .topbar { display: none }
    .swagger-ui .scheme-container { background: #f7f7f7; }
  `,
  customSiteTitle: 'Put On Your Genes API Documentation',
  customfavIcon: '/favicon.ico',
  swaggerOptions: {
    persistAuthorization: true,
    displayRequestDuration: true,
    docExpansion: 'none',
    filter: true,
    showExtensions: true,
    showCommonExtensions: true,
    defaultModelRendering: 'model',
  },
};

// Export swagger middleware setup
export const setupSwagger = (): [string, ...any[]] | null => {
  if (!env.SWAGGER_ENABLED) {
    return null;
  }
  
  return [
    '/api-docs',
    swaggerUi.serve,
    swaggerUi.setup(swaggerSpec, swaggerUiOptions)
  ] as [string, ...any[]];
};