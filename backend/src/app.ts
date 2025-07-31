import express from 'express';
import session from 'express-session';
import { env } from './config/env';
import { logger } from './config/logger';
import { setupSwagger } from './config/swagger';

// Import security middleware
import {
  helmetConfig,
  corsConfig,
  rateLimitConfig,
  hppConfig,
  xssProtection,
  requestLogger,
  securityHeaders,
} from './middleware/security';

// Import error handling middleware
import {
  globalErrorHandler,
  notFoundHandler,
} from './middleware/error';

// Import routes
import { healthRoutes } from './routes/health.routes';

class App {
  public app: express.Application;

  constructor() {
    this.app = express();
    this.initializeMiddleware();
    this.initializeRoutes();
    this.initializeErrorHandling();
  }

  private initializeMiddleware(): void {
    // Trust proxy (important for accurate IP addresses behind load balancers)
    this.app.set('trust proxy', 1);

    // Security headers (Helmet)
    this.app.use(helmetConfig);

    // Custom security headers
    this.app.use(securityHeaders);

    // CORS configuration
    this.app.use(corsConfig);

    // Request logging and correlation ID
    this.app.use(requestLogger);

    // Rate limiting
    this.app.use(rateLimitConfig);

    // HTTP Parameter Pollution protection
    this.app.use(hppConfig);

    // Body parsing with size limits
    this.app.use(express.json({ 
      limit: '1mb',
      type: 'application/json',
    }));
    
    this.app.use(express.urlencoded({ 
      extended: true, 
      limit: '1mb',
      type: 'application/x-www-form-urlencoded',
    }));

    // XSS Protection (custom implementation)
    this.app.use(xssProtection);

    // Session configuration for secure cookies
    this.app.use(session({
      secret: env.SESSION_SECRET,
      name: 'sessionId',
      resave: false,
      saveUninitialized: false,
      cookie: {
        secure: env.NODE_ENV === 'production', // HTTPS only in production
        httpOnly: true, // Prevent XSS attacks
        maxAge: 24 * 60 * 60 * 1000, // 24 hours
        sameSite: 'strict', // CSRF protection
      },
      rolling: true, // Reset expiry on activity
    }));

    // Setup Swagger documentation (if enabled)
    if (env.SWAGGER_ENABLED) {
      const swaggerSetup = setupSwagger();
      if (swaggerSetup && Array.isArray(swaggerSetup) && swaggerSetup.length >= 2) {
        const [path, ...handlers] = swaggerSetup;
        if (typeof path === 'string') {
          this.app.use(path, ...handlers);
          logger.info('Swagger documentation enabled at /api-docs');
        }
      }
    }
  }

  private initializeRoutes(): void {
    // API routes
    this.app.use('/health', healthRoutes);

    // Root endpoint
    this.app.get('/', (req, res) => {
      res.json({
        message: 'Put On Your Genes API',
        version: '1.0.0',
        environment: env.NODE_ENV,
        timestamp: new Date().toISOString(),
        correlationId: req.correlationId,
        documentation: env.SWAGGER_ENABLED ? '/api-docs' : 'disabled',
      });
    });

    // API version prefix for future versioning
    this.app.use('/api/v1/health', healthRoutes);
  }

  private initializeErrorHandling(): void {
    // 404 handler (must be after all routes)
    this.app.use(notFoundHandler);

    // Global error handler (must be last)
    this.app.use(globalErrorHandler);
  }

  public listen(): void {
    const port = env.PORT;
    
    this.app.listen(port, () => {
      logger.info({
        port,
        environment: env.NODE_ENV,
        nodeVersion: process.version,
        pid: process.pid,
        swaggerEnabled: env.SWAGGER_ENABLED,
      }, `🚀 Put On Your Genes API server started on port ${port}`);

      // Log startup configuration (without sensitive data)
      logger.info({
        config: {
          nodeEnv: env.NODE_ENV,
          corsOrigin: env.CORS_ORIGIN,
          rateLimitWindow: env.RATE_LIMIT_WINDOW_MS,
          rateLimitMax: env.RATE_LIMIT_MAX_REQUESTS,
          logLevel: env.LOG_LEVEL,
          swaggerEnabled: env.SWAGGER_ENABLED,
        },
      }, 'Server configuration loaded');
    });

    // Graceful shutdown handling
    this.setupGracefulShutdown();
  }

  private setupGracefulShutdown(): void {
    const gracefulShutdown = (signal: string) => {
      logger.info({ signal }, 'Received shutdown signal, starting graceful shutdown');
      
      process.exit(0);
    };

    // Handle shutdown signals
    process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));
    process.on('SIGINT', () => gracefulShutdown('SIGINT'));

    // Handle uncaught exceptions
    process.on('uncaughtException', (error) => {
      logger.fatal({ error }, 'Uncaught exception occurred');
      process.exit(1);
    });

    // Handle unhandled promise rejections
    process.on('unhandledRejection', (reason, promise) => {
      logger.fatal({ reason, promise }, 'Unhandled promise rejection occurred');
      process.exit(1);
    });
  }
}

export default App;