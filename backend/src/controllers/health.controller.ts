import { Request, Response } from 'express';
import { logger } from '../config/logger';
import { env } from '../config/env';
import { asyncHandler, HealthCheckError } from '../middleware/error';

interface HealthCheckResponse {
  status: 'healthy' | 'unhealthy';
  timestamp: string;
  uptime: number;
  version: string;
  environment: string;
  correlationId: string;
  checks: {
    database: {
      status: 'healthy' | 'unhealthy';
      responseTime?: number;
      error?: string;
    };
    memory: {
      status: 'healthy' | 'unhealthy';
      usage: NodeJS.MemoryUsage;
      thresholds: {
        heapUsedPercent: number;
        maxHeapUsedPercent: number;
      };
    };
  };
}

class HealthController {
  /**
   * @swagger
   * /health:
   *   get:
   *     summary: Get application health status
   *     description: Returns comprehensive health information including database connectivity and memory usage
   *     tags: [Health]
   *     security: [] # No authentication required for health checks
   *     responses:
   *       200:
   *         description: Application is healthy
   *         content:
   *           application/json:
   *             schema:
   *               $ref: '#/components/schemas/HealthCheck'
   *       503:
   *         description: Application is unhealthy
   *         content:
   *           application/json:
   *             schema:
   *               $ref: '#/components/schemas/HealthCheck'
   */
  public health = asyncHandler(async (req: Request, res: Response) => {
    const startTime = Date.now();
    
    try {
      // Check database connectivity
      const dbCheck = await this.checkDatabase();
      
      // Check memory usage
      const memoryCheck = this.checkMemory();
      
      // Determine overall health status
      const isHealthy = dbCheck.status === 'healthy' && memoryCheck.status === 'healthy';
      
      const healthResponse: HealthCheckResponse = {
        status: isHealthy ? 'healthy' : 'unhealthy',
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
        version: process.env.npm_package_version || '1.0.0',
        environment: env.NODE_ENV,
        correlationId: req.correlationId || 'unknown',
        checks: {
          database: dbCheck,
          memory: memoryCheck,
        },
      };

      const responseTime = Date.now() - startTime;
      
      logger.info({
        correlationId: req.correlationId,
        healthStatus: healthResponse.status,
        responseTime,
        checks: {
          database: dbCheck.status,
          memory: memoryCheck.status,
        },
      }, 'Health check completed');

      const statusCode = isHealthy ? 200 : 503;
      res.status(statusCode).json(healthResponse);
      
    } catch (error) {
      logger.error({
        correlationId: req.correlationId,
        error,
      }, 'Health check failed unexpectedly');
      
      throw new HealthCheckError('system', 'Unexpected error during health check');
    }
  });

  /**
   * @swagger
   * /health/ready:
   *   get:
   *     summary: Get application readiness status
   *     description: Returns readiness status for Kubernetes readiness probes
   *     tags: [Health]
   *     security: [] # No authentication required
   *     responses:
   *       200:
   *         description: Application is ready to serve requests
   *         content:
   *           application/json:
   *             schema:
   *               type: object
   *               properties:
   *                 status:
   *                   type: string
   *                   enum: [ready]
   *                 timestamp:
   *                   type: string
   *                   format: date-time
   *       503:
   *         description: Application is not ready
   *         content:
   *           application/json:
   *             schema:
   *               $ref: '#/components/schemas/Error'
   */
  public readiness = asyncHandler(async (req: Request, res: Response) => {
    try {
      // Basic readiness check - ensure database is accessible
      const dbCheck = await this.checkDatabase();
      
      if (dbCheck.status !== 'healthy') {
        throw new HealthCheckError('database', 'Database not ready');
      }
      
      res.status(200).json({
        status: 'ready',
        timestamp: new Date().toISOString(),
        correlationId: req.correlationId,
      });
      
    } catch (error) {
      logger.warn({
        correlationId: req.correlationId,
        error,
      }, 'Readiness check failed');
      
      res.status(503).json({
        error: 'Service Unavailable',
        message: 'Application is not ready to serve requests',
        correlationId: req.correlationId,
        timestamp: new Date().toISOString(),
        path: req.path,
      });
    }
  });

  /**
   * @swagger
   * /health/live:
   *   get:
   *     summary: Get application liveness status
   *     description: Returns liveness status for Kubernetes liveness probes
   *     tags: [Health]
   *     security: [] # No authentication required
   *     responses:
   *       200:
   *         description: Application is alive
   *         content:
   *           application/json:
   *             schema:
   *               type: object
   *               properties:
   *                 status:
   *                   type: string
   *                   enum: [alive]
   *                 timestamp:
   *                   type: string
   *                   format: date-time
   *                 uptime:
   *                   type: number
   */
  public liveness = asyncHandler(async (req: Request, res: Response) => {
    // Liveness check - just ensure the process is running
    res.status(200).json({
      status: 'alive',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
      correlationId: req.correlationId,
    });
  });

  private async checkDatabase(): Promise<{
    status: 'healthy' | 'unhealthy';
    responseTime?: number;
    error?: string;
  }> {
    const startTime = Date.now();
    
    try {
      // TODO: Replace with actual Prisma database check when Prisma is configured
      // For now, simulate a database check
      await new Promise(resolve => setTimeout(resolve, 10)); // Simulate DB query
      
      const responseTime = Date.now() - startTime;
      
      // Check if response time is acceptable (under 1 second)
      if (responseTime > 1000) {
        return {
          status: 'unhealthy',
          responseTime,
          error: 'Database response time too slow',
        };
      }
      
      return {
        status: 'healthy',
        responseTime,
      };
      
    } catch (error) {
      const responseTime = Date.now() - startTime;
      
      logger.error({
        error,
        responseTime,
      }, 'Database health check failed');
      
      return {
        status: 'unhealthy',
        responseTime,
        error: error instanceof Error ? error.message : 'Unknown database error',
      };
    }
  }

  private checkMemory(): {
    status: 'healthy' | 'unhealthy';
    usage: NodeJS.MemoryUsage;
    thresholds: {
      heapUsedPercent: number;
      maxHeapUsedPercent: number;
    };
  } {
    const memoryUsage = process.memoryUsage();
    const heapUsedPercent = (memoryUsage.heapUsed / memoryUsage.heapTotal) * 100;
    const maxHeapUsedPercent = 90; // Threshold for unhealthy status
    
    const isHealthy = heapUsedPercent < maxHeapUsedPercent;
    
    return {
      status: isHealthy ? 'healthy' : 'unhealthy',
      usage: memoryUsage,
      thresholds: {
        heapUsedPercent,
        maxHeapUsedPercent,
      },
    };
  }
}

export const healthController = new HealthController();