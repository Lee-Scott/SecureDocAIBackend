# Put On Your Genes Backend API

HIPAA-compliant Node.js backend for the Put On Your Genes healthcare platform, built with TypeScript, Express, and comprehensive security middleware.

## 🏥 Healthcare Platform Features

- **HIPAA Compliance**: Built-in security measures for healthcare data protection
- **Genetics Integration**: Support for genetic testing and analysis
- **Lab Tests**: Integration with laboratory testing services
- **Supplements**: E-commerce functionality for supplement ordering
- **Security First**: OWASP security patterns and comprehensive audit logging

## 🛡️ Security Features

- **OWASP Security Headers**: Helmet.js with comprehensive CSP
- **Rate Limiting**: Configurable rate limiting with healthcare-appropriate limits
- **Input Validation**: Express-validator and Zod for comprehensive validation
- **XSS Protection**: Custom XSS protection middleware
- **CORS**: Restrictive CORS configuration
- **Audit Logging**: Comprehensive audit trails for HIPAA compliance
- **Data Masking**: PII and PHI data masking in logs
- **Session Security**: Secure session configuration with proper cookies

## 🚀 Getting Started

### Prerequisites

- Node.js >= 20.6.0
- npm >= 10.0.0
- PostgreSQL database

### Installation

1. **Clone and navigate to backend directory**:
   ```bash
   cd backend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Environment setup**:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

4. **Database setup**:
   ```bash
   npm run prisma:generate
   npm run prisma:migrate
   ```

### Development

```bash
# Start development server
npm run dev

# Start with watch mode
npm run dev:watch

# Build for production
npm run build

# Start production server
npm start
```

### Testing

```bash
# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

### Code Quality

```bash
# Lint code
npm run lint

# Fix linting issues
npm run lint:fix

# Format code with Prettier
npx prettier --write src/
```

## 📁 Project Structure

```
src/
├── config/          # Configuration files
│   ├── env.ts       # Environment validation
│   ├── logger.ts    # Pino logger configuration
│   └── swagger.ts   # API documentation
├── controllers/     # Route controllers
├── middleware/      # Express middleware
│   ├── security.ts  # OWASP security middleware
│   ├── validation.ts # Input validation
│   └── error.ts     # Error handling
├── models/          # Data models (Prisma)
├── routes/          # Express routes
├── services/        # Business logic
├── utils/           # Utility functions
├── types/           # TypeScript type definitions
├── tests/           # Test files
└── index.ts         # Application entry point
```

## 🔧 Configuration

### Environment Variables

Required environment variables (see `.env.example`):

- `NODE_ENV`: Environment (development/production/test)
- `PORT`: Server port
- `DATABASE_URL`: PostgreSQL connection string
- `JWT_SECRET`: JWT signing secret (min 32 characters)
- `SESSION_SECRET`: Session signing secret (min 32 characters)
- `CORS_ORIGIN`: Allowed CORS origins

### Security Configuration

- **Rate Limiting**: 100 requests per 15 minutes (configurable)
- **Body Size Limit**: 1MB maximum
- **Session**: 24-hour expiry with rolling refresh
- **CORS**: Restrictive whitelist-based configuration
- **Headers**: Comprehensive security headers via Helmet

## 📊 API Documentation

When `SWAGGER_ENABLED=true`, API documentation is available at:
- **Development**: http://localhost:3000/api-docs
- **Production**: https://api.putonyourgenes.com/api-docs

## 🔍 Health Monitoring

Health check endpoints for monitoring and Kubernetes:

- `GET /health` - Comprehensive health check
- `GET /health/ready` - Readiness probe
- `GET /health/live` - Liveness probe
- `GET /api/v1/health` - Versioned health check

## 📝 Logging

Structured logging with Pino:

- **Development**: Pretty-printed logs
- **Production**: JSON structured logs
- **PII Masking**: Automatic masking of sensitive healthcare data
- **Correlation IDs**: Request tracking for debugging

## 🛡️ HIPAA Compliance

- **Audit Logging**: All data access logged
- **Data Encryption**: Encryption at rest and in transit
- **Access Controls**: Role-based access control
- **Data Masking**: PII/PHI masked in logs
- **Secure Sessions**: HTTPS-only secure cookies
- **Input Sanitization**: Comprehensive input validation

## 🧪 Testing

- **Unit Tests**: Jest with TypeScript support
- **Integration Tests**: Supertest for API testing
- **Coverage Reports**: Comprehensive test coverage
- **Security Testing**: ESLint security rules
- **Custom Matchers**: Healthcare-specific test utilities

## 🔄 Database

- **ORM**: Prisma for type-safe database operations
- **Migrations**: Automatic schema migrations
- **Connection Pooling**: Built-in connection management
- **Audit Trails**: Comprehensive audit logging

## 📈 Performance

- **Correlation IDs**: Request tracking
- **Response Time Monitoring**: Built-in metrics
- **Memory Monitoring**: Health check memory usage
- **Database Monitoring**: Connection health checks

## 🚢 Deployment

### Production Checklist

- [ ] Set `NODE_ENV=production`
- [ ] Configure secure `JWT_SECRET` and `SESSION_SECRET`
- [ ] Set up PostgreSQL with SSL
- [ ] Configure proper CORS origins
- [ ] Enable HTTPS
- [ ] Set up log aggregation
- [ ] Configure monitoring and alerting
- [ ] Set up backup procedures

### Docker Support

```dockerfile
# Dockerfile example
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY dist ./dist
EXPOSE 3000
CMD ["node", "dist/index.js"]
```

## 🤝 Contributing

1. Follow TypeScript and ESLint configurations
2. Maintain test coverage above 80%
3. Follow security best practices
4. Update documentation for API changes
5. Test HIPAA compliance requirements

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact: support@putonyourgenes.com

## 📄 License

MIT License - see LICENSE file for details.