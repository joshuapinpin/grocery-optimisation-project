# Docker & AWS Deployment Files Summary

## Files Created

### Docker Configuration
- **`backend/Dockerfile`** - Multi-stage build for Spring Boot backend
  - Builds JAR with Maven
  - Runs with OpenJDK 21 Alpine for minimal size
  
- **`frontend/Dockerfile`** - Multi-stage build for React frontend
  - Builds with Node.js and Vite
  - Serves with Nginx
  
- **`frontend/nginx.conf`** - Nginx configuration
  - Serves React SPA with proper routing
  - Proxies API requests to backend container
  
- **`docker-compose.yml`** - Orchestration for local development
  - PostgreSQL database container
  - Spring Boot backend container
  - React frontend container with Nginx
  - Health checks and networking configured

### Production Configuration
- **`backend/src/main/resources/application-prod.properties`** - Production Spring Boot config
  - Environment variables for database connection
  - Optimized logging levels
  
- **`.dockerignore`** - Excludes unnecessary files from Docker builds

### AWS Deployment Files
- **`AWS_DEPLOYMENT_GUIDE.md`** - Comprehensive deployment guide
  - 4 deployment options (ECS, Lightsail, App Runner, Elastic Beanstalk)
  - Step-by-step instructions
  - Cost estimation
  
- **`QUICK_START.md`** - Quick reference for local testing and deployment
  
- **`ecs-task-definition-backend.json`** - ECS Fargate task definition for backend
  - Health checks configured
  - CloudWatch logging setup
  - AWS Secrets Manager integration
  
- **`ecs-task-definition-frontend.json`** - ECS Fargate task definition for frontend
  - Health checks configured
  
- **`lightsail-containers.json`** - AWS Lightsail container configuration
  - Simplest option for small deployments
  
- **`cloudformation-template.json`** - Infrastructure as Code for ECS deployment
  - VPC with public/private subnets
  - RDS PostgreSQL database
  - ECS cluster
  - Security groups and IAM roles

### CI/CD Pipeline
- **`.github/workflows/docker-build-push.yml`** - GitHub Actions workflow
  - Builds and pushes Docker images to ECR
  - Updates ECS services on main branch push
  - Requires AWS IAM role setup

### Build Script
- **`build-docker.sh`** - Local Docker build script
  - Builds both images
  - Makes it easy to test locally

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      AWS Environment                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │             Application Load Balancer               │   │
│  └────────────────┬────────────────────────────────────┘   │
│                   │                                         │
│         ┌─────────┴─────────┐                              │
│         │                   │                              │
│  ┌──────▼─────┐      ┌──────▼─────┐                       │
│  │  Frontend   │      │   Backend   │                       │
│  │  (Nginx +   │      │  (Spring    │                       │
│  │   React)    │      │   Boot)     │                       │
│  │  Port 80    │      │  Port 8080  │                       │
│  └─────────────┘      └──────┬──────┘                       │
│                              │                              │
│                       ┌──────▼──────┐                       │
│                       │     RDS     │                       │
│                       │ PostgreSQL  │                       │
│                       │ Port 5432   │                       │
│                       └─────────────┘                       │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Deployment Options Comparison

| Option | Cost | Setup Time | Best For | Maintenance |
|--------|------|-----------|----------|------------|
| **Lightsail** | ~$3.50/mo | 10 min | Small projects | Low |
| **ECS Fargate** | ~$90/mo | 30 min | Medium projects | Medium |
| **App Runner** | ~$1-5/mo | 5 min | Auto-deployment | Low |
| **Elastic Beanstalk** | ~$50/mo | 20 min | Multi-container | Medium |

## Quick Start Checklist

- [ ] Test locally: `docker-compose up -d`
- [ ] Verify frontend: http://localhost
- [ ] Verify backend: http://localhost:8080/api
- [ ] Verify API docs: http://localhost:8080/api/swagger-ui.html
- [ ] Choose AWS deployment option
- [ ] Create AWS account and configure CLI
- [ ] Set up database (RDS or Lightsail managed database)
- [ ] Build and push Docker images
- [ ] Deploy using chosen option
- [ ] Configure domain and HTTPS
- [ ] Set up monitoring and backups

## Key Features

✅ Multi-stage Docker builds (optimized image sizes)
✅ Health checks configured for both containers
✅ Environment variable configuration for different environments
✅ CloudWatch logging integration
✅ AWS Secrets Manager integration for sensitive data
✅ GitHub Actions CI/CD pipeline
✅ Production-ready Nginx configuration
✅ PostgreSQL with automatic backups
✅ VPC and security group setup
✅ Auto-scaling ready

## Environment Variables Reference

### Backend (Spring Boot)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/bagnsave_db_v2
SPRING_DATASOURCE_USERNAME=bashServiceAccount
SPRING_DATASOURCE_PASSWORD=<secure_password>
SPRING_PROFILES_ACTIVE=prod
```

### Frontend
```
VITE_API_URL=<backend_url>/api  # Optional, uses proxy by default
```

## Security Best Practices Implemented

1. ✅ Secrets stored in AWS Secrets Manager (not in code)
2. ✅ Health checks prevent unhealthy containers from receiving traffic
3. ✅ Database runs in private subnet (not internet-exposed)
4. ✅ Security groups restrict traffic to only necessary ports
5. ✅ Separate IAM roles for task execution and task permissions
6. ✅ Multi-stage Docker builds reduce attack surface

## Next Steps

1. **Test locally first**: Follow QUICK_START.md
2. **Set up AWS**: Configure account and CLI
3. **Choose deployment**: Pick Lightsail for simplicity, ECS for scale
4. **Deploy**: Follow AWS_DEPLOYMENT_GUIDE.md
5. **Monitor**: Use CloudWatch and set up alarms
6. **Iterate**: Use GitHub Actions for continuous deployment

## Files to Update Before Production

1. **`backend/src/main/resources/application-prod.properties`**
   - Update database connection string if using external RDS
   
2. **`frontend/nginx.conf`**
   - Update backend URL if frontend and backend are on different domains
   
3. **`ecs-task-definition-backend.json`**
   - Update AWS account ID and image URL
   
4. **`.github/workflows/docker-build-push.yml`**
   - Add AWS account ID and credentials setup

## Troubleshooting Commands

```bash
# View Docker logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Test backend health
curl http://localhost:8080/api/actuator/health

# Test database connection
docker-compose exec backend psql -h postgres -U bashServiceAccount -d bagnsave_db_v2 -c "SELECT 1"

# Rebuild without cache
docker-compose build --no-cache

# Clean up everything
docker-compose down -v
```

For detailed information, see AWS_DEPLOYMENT_GUIDE.md and QUICK_START.md

