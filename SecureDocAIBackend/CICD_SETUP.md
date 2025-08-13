# CI/CD Pipeline Setup Guide

This guide explains how to configure the GitHub Actions workflows for your SecureDocAI project.

## Overview

Your project now has three GitHub Actions workflows:

1. **`ci.yml`** - Backend CI with PostgreSQL testing
2. **`frontend-ci.yml`** - Frontend CI with Node.js/TypeScript
3. **`deploy.yml`** - Production deployment to Railway + Netlify

## Required GitHub Secrets

To enable deployment, you need to configure these secrets in your GitHub repository:

### Go to: Settings → Secrets and variables → Actions → New repository secret

### Backend Deployment (Railway)
```
RAILWAY_TOKEN=your_railway_api_token
RAILWAY_SERVICE_ID=your_railway_service_id
DATABASE_URL=postgresql://user:password@host:port/database
DATABASE_USERNAME=your_prod_db_username
DATABASE_PASSWORD=your_prod_db_password
JWT_SECRET=your_jwt_secret_key_minimum_32_characters
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.netlify.app
```

### Frontend Deployment (Netlify)
```
NETLIFY_AUTH_TOKEN=your_netlify_personal_access_token
NETLIFY_SITE_ID=your_netlify_site_id
VITE_API_BASE_URL=https://your-backend-domain.railway.app
VITE_APP_TITLE=SecureDocAI
```

## Platform Setup Instructions

### 1. Railway (Backend Hosting)
1. Sign up at [railway.app](https://railway.app)
2. Create a new project
3. Add PostgreSQL database service
4. Deploy your Spring Boot app
5. Get your Railway token from Account Settings → Tokens
6. Get service ID from your project dashboard URL

### 2. Netlify (Frontend Hosting)
1. Sign up at [netlify.com](https://netlify.com)
2. Create a new site (can be empty initially)
3. Get your site ID from Site settings → General → Site details
4. Generate personal access token from User settings → Applications → Personal access tokens

### 3. Alternative Platforms (Cost-Effective Options)

#### Render (Alternative to Railway)
- Free tier available
- Automatic deployments from GitHub
- Built-in PostgreSQL

#### Vercel (Alternative to Netlify)
- Excellent for React/TypeScript apps
- Free tier with good limits
- Easy GitHub integration

## Workflow Features

### Backend CI (`ci.yml`)
- ✅ Java 21 setup
- ✅ PostgreSQL service for integration tests
- ✅ Maven dependency caching
- ✅ JaCoCo coverage reports
- ✅ Triggers on main and feature branches

### Frontend CI (`frontend-ci.yml`)
- ✅ Node.js 18 setup
- ✅ TypeScript type checking
- ✅ ESLint code quality checks
- ✅ Jest test execution with coverage
- ✅ Build artifact generation
- ✅ Path-based triggering (only runs when frontend changes)

### Deployment (`deploy.yml`)
- ✅ Production-only deployment (main branch)
- ✅ Manual deployment trigger option
- ✅ Separate backend and frontend deployment jobs
- ✅ Environment variable injection
- ✅ Build verification before deployment

## Cost Optimization

### Free Tier Limits
- **Railway**: $5/month credit (enough for small apps)
- **Netlify**: 100GB bandwidth, 300 build minutes/month
- **GitHub Actions**: 2000 minutes/month for private repos

### Tips to Minimize Costs
1. Use path-based triggers to avoid unnecessary builds
2. Cache dependencies (already configured)
3. Use manual deployment triggers for non-critical updates
4. Monitor usage in platform dashboards

## Security Best Practices

### Environment Variables
- Never commit secrets to code
- Use different secrets for staging/production
- Rotate JWT secrets regularly
- Use strong database passwords

### Database Security
- Enable SSL connections in production
- Use connection pooling
- Regular backups (Railway/Render handle this)

## Testing the Setup

1. **Test CI workflows**: Push a feature branch and verify tests run
2. **Test deployment**: Merge to main and check deployment logs
3. **Verify environment**: Check that your app loads with correct API connections

## Troubleshooting

### Common Issues
- **Build failures**: Check Java/Node versions match local development
- **Database connection**: Verify PostgreSQL service is healthy
- **Deployment failures**: Check all required secrets are configured
- **CORS errors**: Ensure frontend URL is in CORS_ALLOWED_ORIGINS

### Debugging Steps
1. Check GitHub Actions logs for specific error messages
2. Verify all secrets are configured correctly
3. Test database connections in Railway/Render dashboards
4. Check platform-specific logs for deployment issues

## Next Steps

After setting up CI/CD:
1. Configure monitoring and alerting
2. Set up staging environment
3. Add end-to-end tests
4. Implement database migrations
5. Add performance monitoring

## Support

- Railway: [docs.railway.app](https://docs.railway.app)
- Netlify: [docs.netlify.com](https://docs.netlify.com)
- GitHub Actions: [docs.github.com/actions](https://docs.github.com/en/actions)
