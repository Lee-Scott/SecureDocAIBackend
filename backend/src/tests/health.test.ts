import request from 'supertest';
import App from '../app';
import { healthController } from '../controllers/health.controller';
import { createMockRequest, createMockResponse, createMockNext } from './setup';

describe('Health Controller', () => {
  let app: App;
  let server: any;

  beforeAll(() => {
    app = new App();
    server = app.app;
  });

  describe('GET /health', () => {
    it('should return health status', async () => {
      const response = await request(server)
        .get('/health');

      // Health check can return either 200 (healthy) or 503 (unhealthy)
      expect([200, 503]).toContain(response.status);

      expect(response.body).toMatchObject({
        status: expect.stringMatching(/^(healthy|unhealthy)$/),
        timestamp: expect.any(String),
        uptime: expect.any(Number),
        version: expect.any(String),
        environment: 'test',
        correlationId: expect.any(String),
        checks: {
          database: {
            status: expect.stringMatching(/^(healthy|unhealthy)$/),
          },
          memory: {
            status: expect.stringMatching(/^(healthy|unhealthy)$/),
            usage: expect.any(Object),
            thresholds: expect.any(Object),
          },
        },
      });
    });

    it('should include correlation ID in response', async () => {
      const response = await request(server)
        .get('/health');

      expect([200, 503]).toContain(response.status);
      expect(response.body.correlationId).toBeDefined();
      expect(response.body.correlationId).toMatch(/^req_/);
    });

    it('should include proper headers', async () => {
      const response = await request(server)
        .get('/health');

      expect([200, 503]).toContain(response.status);
      expect(response.headers['x-correlation-id']).toBeDefined();
      expect(response.headers['x-content-type-options']).toBe('nosniff');
      expect(response.headers['x-frame-options']).toBe('DENY');
    });
  });

  describe('GET /health/ready', () => {
    it('should return readiness status', async () => {
      const response = await request(server)
        .get('/health/ready')
        .expect(200);

      expect(response.body).toMatchObject({
        status: 'ready',
        timestamp: expect.any(String),
        correlationId: expect.any(String),
      });
    });
  });

  describe('GET /health/live', () => {
    it('should return liveness status', async () => {
      const response = await request(server)
        .get('/health/live')
        .expect(200);

      expect(response.body).toMatchObject({
        status: 'alive',
        timestamp: expect.any(String),
        uptime: expect.any(Number),
        correlationId: expect.any(String),
      });
    });
  });

  describe('Health Controller Unit Tests', () => {
    it('should handle health check with mock request/response', async () => {
      const mockReq = createMockRequest();
      const mockRes = createMockResponse();
      const mockNext = createMockNext();

      await healthController.health(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(expect.any(Number));
      expect(mockRes.json).toHaveBeenCalledWith(
        expect.objectContaining({
          status: expect.stringMatching(/^(healthy|unhealthy)$/),
          timestamp: expect.any(String),
          uptime: expect.any(Number),
        })
      );
    });

    it('should handle readiness check with mock request/response', async () => {
      const mockReq = createMockRequest();
      const mockRes = createMockResponse();
      const mockNext = createMockNext();

      await healthController.readiness(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(expect.any(Number));
      expect(mockRes.json).toHaveBeenCalledWith(
        expect.objectContaining({
          status: expect.any(String),
          timestamp: expect.any(String),
        })
      );
    });

    it('should handle liveness check with mock request/response', async () => {
      const mockReq = createMockRequest();
      const mockRes = createMockResponse();
      const mockNext = createMockNext();

      await healthController.liveness(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(
        expect.objectContaining({
          status: 'alive',
          timestamp: expect.any(String),
          uptime: expect.any(Number),
        })
      );
    });
  });

  describe('API Versioning', () => {
    it('should work with versioned endpoint', async () => {
      const response = await request(server)
        .get('/api/v1/health')
        .expect(200);

      expect(response.body.status).toMatch(/^(healthy|unhealthy)$/);
    });
  });

  describe('Root Endpoint', () => {
    it('should return API information', async () => {
      const response = await request(server)
        .get('/')
        .expect(200);

      expect(response.body).toMatchObject({
        message: 'Put On Your Genes API',
        version: '1.0.0',
        environment: 'test',
        timestamp: expect.any(String),
        correlationId: expect.any(String),
        documentation: expect.any(String),
      });
    });
  });
});