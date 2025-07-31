import 'dotenv-safe/config';
import App from './app';
import { logger } from './config/logger';
import { env } from './config/env';

// Validate environment variables on startup
logger.info('Starting Put On Your Genes Backend API...');
logger.info({
  nodeVersion: process.version,
  environment: env.NODE_ENV,
  port: env.PORT,
}, 'Environment validated successfully');

// Create and start the application
const app = new App();
app.listen();