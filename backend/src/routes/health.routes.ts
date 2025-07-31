import { Router } from 'express';
import { healthController } from '../controllers/health.controller';

const router = Router();

/**
 * Health check routes
 * These endpoints are used for monitoring and health checking
 */

// Main health check endpoint
router.get('/', healthController.health);

// Kubernetes readiness probe endpoint
router.get('/ready', healthController.readiness);

// Kubernetes liveness probe endpoint  
router.get('/live', healthController.liveness);

export const healthRoutes = router;