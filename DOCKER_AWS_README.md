# BagnSave: Docker & AWS Deployment Guide

> Complete guide to containerizing and deploying BagnSave frontend and backend to AWS

## 📋 Table of Contents

1. [Overview](#overview)
2. [What's Been Created](#whats-been-created)
3. [Quick Start (Local Testing)](#quick-start-local-testing)
4. [AWS Deployment](#aws-deployment)
5. [Architecture](#architecture)
6. [Troubleshooting](#troubleshooting)

## 🎯 Overview

BagnSave is now ready to be containerized and deployed to AWS! Here's what you need to know:

### Technology Stack
- **Frontend**: React + Vite, served by Nginx
- **Backend**: Spring Boot 3.5.14 with Java 21
- **Database**: PostgreSQL 17
- **Containers**: Docker with Docker Compose for local development
- **AWS Options**: Lightsail, ECS Fargate, App Runner, or Elastic Beanstalk

### Architecture
```
User Browser
    ↓
ALB / Public URL
    ↓
Nginx (Frontend Container)
    ├→ Serves React SPA
    └→ Proxies /api to Backend
         ↓
    Spring Boot (Backend Container)
         ↓
    RDS PostgreSQL
```

## ✨ What's Been Created

### Docker Files
| File | Purpose |
|------|---------|
| `backend/Dockerfile` | Multi-stage build for Spring Boot (optimized size) |
| `frontend/Dockerfile` | Multi-stage build for React + Nginx |
| `frontend/nginx.conf` | Nginx configuration for SPA routing & API proxy |
| `.dockerignore` | Excludes unnecessary files from builds |

### Configuration Files
| File | Purpose |
|------|---------|
| `docker-compose.yml` | Local development with all services |
| `backend/src/main/resources/application-prod.properties` | Production Spring Boot config |

### AWS Deployment Files
| File | Purpose |
|------|---------|
| `ecs-task-definition-backend.json` | ECS Fargate task for backend |
| `ecs-task-definition-frontend.json` | ECS Fargate task for frontend |
| `lightsail-containers.json` | AWS Lightsail container config |
| `cloudformation-template.json` | Infrastructure as Code (VPC, RDS, ECS, security) |
| `.github/workflows/docker-build-push.yml` | CI/CD pipeline for automated deployment |

### Documentation
| File | Purpose |
|------|---------|
| `DOCKER_SETUP_SUMMARY.md` | Complete overview of setup |
| `QUICK_START.md` | Quick reference for common tasks |
| `AWS_DEPLOYMENT_GUIDE.md` | Detailed deployment instructions |
| `README.md` | This file |

### Helper Scripts
| File | Purpose |
|------|---------|
| `build-docker.sh` | Build Docker images locally |
| `aws-deploy.sh` | Interactive AWS deployment helper |

## 🚀 Quick Start (Local Testing)

### Prerequisites
- Docker Desktop installed ([download](https://www.docker.com/products/docker-desktop))
- No changes needed to your code!

### Step 1: Test Locally
```bash
cd /Users/nickkho/Desktop/BagnSave

# Build and run all containers
docker-compose up -d

# View logs
docker-compose logs -f

# Stop everything
docker-compose down
```

### Step 2: Access the Application
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/api/swagger-ui.html
- **Database**: localhost:5432

### Step 3: Verify Everything Works
```bash
# Frontend should load
curl http://localhost

# Backend should respond
curl http://localhost:8080/api/swagger-ui.html

# Database should be reachable
docker-compose exec postgres psql -U bashServiceAccount -d bagnsave_db_v2 -c "SELECT 1"
```

### Troubleshooting Local Setup
```bash
# View specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Restart a service
docker-compose restart backend

# Rebuild an image without cache
docker-compose build --no-cache backend

# Remove all containers and volumes
docker-compose down -v
```

## ☁️ AWS Deployment

### Recommended Path: AWS Lightsail (Easiest)

**Why Lightsail?**
- Simplest to set up
- $3.50/month for small container service
- Perfect for getting started
- No complex configuration needed

### Step 1: Set Up AWS Account
```bash
# Install AWS CLI (if not already installed)
brew install awscli

# Configure credentials
aws configure
# Enter: Access Key ID, Secret Access Key, Region (us-east-1)
```

### Step 2: Run Automated Setup
```bash
chmod +x aws-deploy.sh

# Option A: Interactive menu
./aws-deploy.sh

# Option B: Full setup in one command
./aws-deploy.sh full-setup us-east-1

# Option C: Manual steps
./aws-deploy.sh create-repos us-east-1
./aws-deploy.sh build-push us-east-1
./aws-deploy.sh create-cluster us-east-1
./aws-deploy.sh status us-east-1
```

### Step 3: Set Up Database
```bash
# Create RDS database via AWS Console or:
aws rds create-db-instance \
  --db-instance-identifier bagnsave-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 17.1 \
  --master-username bashServiceAccount \
  --master-user-password <STRONG_PASSWORD> \
  --allocated-storage 20 \
  --db-name bagnsave_db_v2 \
  --publicly-accessible true
```

### Step 4: Deploy Using Lightsail
```bash
# Create container service
aws lightsail create-container-service \
  --service-name bagnsave \
  --power small \
  --region us-east-1

# Push images (script already does this)
./aws-deploy.sh build-push us-east-1

# Deploy
aws lightsail create-container-service-deployment \
  --service-name bagnsave \
  --cli-input-json file://lightsail-containers.json \
  --region us-east-1

# Get public endpoint
aws lightsail get-container-services \
  --service-name bagnsave \
  --region us-east-1
```

### Alternative: AWS App Runner (Simplest)
1. Connect GitHub to AWS App Runner
2. Select this repository
3. Configure backend service (Dockerfile: backend/Dockerfile)
4. Configure frontend service (Dockerfile: frontend/Dockerfile)
5. Deploy!

### Alternative: ECS Fargate (Most Scalable)
```bash
# Deploy with CloudFormation
aws cloudformation create-stack \
  --stack-name bagnsave-stack \
  --template-body file://cloudformation-template.json \
  --parameters \
    ParameterKey=DBPassword,ParameterValue=<PASSWORD> \
    ParameterKey=ECRBackendImage,ParameterValue=<IMAGE_URL> \
    ParameterKey=ECRFrontendImage,ParameterValue=<IMAGE_URL> \
  --capabilities CAPABILITY_IAM
```

## 🏗️ Architecture

### Local Docker Compose
```
┌─────────────────────────────────────────┐
│        Local Development                 │
├─────────────────────────────────────────┤
│  Frontend                                │
│  ├─ Nginx:80                             │
│  └─ Proxy → Backend:8080                 │
│                                          │
│  Backend                                 │
│  ├─ Spring Boot:8080                     │
│  └─ Connects → DB:5432                   │
│                                          │
│  PostgreSQL                              │
│  └─ Database:5432                        │
└─────────────────────────────────────────┘
```

### AWS Production
```
┌────────────────────────────────────────────────────────┐
│                    AWS Cloud                            │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │          Application Load Balancer               │  │
│  │  (Public endpoint: example.com)                  │  │
│  └────────────────┬─────────────────────────────────┘  │
│                   │                                     │
│         ┌─────────┴─────────┐                          │
│         │                   │                          │
│  ┌──────▼──────┐    ┌──────▼──────┐                   │
│  │  Frontend    │    │  Backend     │                   │
│  │  (Container) │    │  (Container) │                   │
│  │  Port 80     │    │  Port 8080   │                   │
│  └──────────────┘    └──────┬───────┘                   │
│                             │                          │
│                      ┌──────▼───────┐                  │
│                      │ RDS Database  │                  │
│                      │ PostgreSQL    │                  │
│                      │ Port 5432     │                  │
│                      │ (Private)     │                  │
│                      └───────────────┘                  │
└────────────────────────────────────────────────────────┘
```

## 📊 Cost Comparison

| Service | Cost/Month | Best For | Setup Time |
|---------|-----------|----------|-----------|
| **Lightsail** | ~$3.50 | Small projects, learning | 10 min |
| **App Runner** | ~$1-5 | Auto-deployment | 5 min |
| **ECS Fargate** | ~$90 | Production, scaling | 30 min |
| **Elastic Beanstalk** | ~$50 | Multi-container apps | 20 min |

## 🔒 Security Best Practices

✅ Implemented in this setup:
- Secrets stored in AWS Secrets Manager (not in code)
- Health checks prevent unhealthy containers from serving traffic
- Database runs in private subnet (not exposed to internet)
- Security groups restrict traffic to necessary ports only
- Multi-stage Docker builds minimize attack surface
- Separate IAM roles for different permissions

## 📚 Environment Variables

### Backend Configuration
```properties
# Database Connection
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/bagnsave_db_v2
SPRING_DATASOURCE_USERNAME=bashServiceAccount
SPRING_DATASOURCE_PASSWORD=<your_secure_password>

# Spring Configuration
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
```

### Frontend Configuration
```env
# Backend URL (optional - nginx proxies by default)
VITE_API_URL=https://api.yourdomain.com
```

## 🔧 Monitoring & Debugging

### View Logs
```bash
# Docker Compose
docker-compose logs -f backend
docker-compose logs -f frontend

# AWS CloudWatch
aws logs tail /ecs/bagnsave-backend --follow
aws logs tail /ecs/bagnsave-frontend --follow
```

### Health Checks
```bash
# Frontend
curl http://localhost

# Backend
curl http://localhost:8080/api/actuator/health

# Database
psql -h localhost -U bashServiceAccount -d bagnsave_db_v2 -c "SELECT 1"
```

### Monitor Resources
```bash
# Docker stats
docker stats

# AWS resources
./aws-deploy.sh status us-east-1
```

## 📋 Deployment Checklist

- [ ] **Local Testing**
  - [ ] Run `docker-compose up`
  - [ ] Verify frontend loads at http://localhost
  - [ ] Verify API responds at http://localhost:8080/api
  - [ ] Test basic functionality

- [ ] **AWS Setup**
  - [ ] Create AWS account
  - [ ] Configure AWS CLI (`aws configure`)
  - [ ] Choose deployment option

- [ ] **Database Setup**
  - [ ] Create RDS PostgreSQL instance
  - [ ] Note the endpoint
  - [ ] Update environment variables

- [ ] **Build & Push**
  - [ ] Run `./aws-deploy.sh build-push us-east-1`
  - [ ] Verify images in ECR

- [ ] **Deploy**
  - [ ] Deploy using Lightsail/ECS/App Runner
  - [ ] Test application via public URL

- [ ] **Post-Deployment**
  - [ ] Configure domain name
  - [ ] Set up HTTPS/SSL certificate
  - [ ] Enable monitoring
  - [ ] Set up backups
  - [ ] Configure auto-scaling

## 🆘 Troubleshooting

### Frontend shows 502 Bad Gateway
- Backend container may not be running or healthy
- Check: `docker-compose logs backend`
- Verify database connection: `docker-compose exec backend curl http://localhost:8080/api/actuator/health`

### Backend can't connect to database
```bash
# Check database is running
docker-compose ps postgres

# Test connection
docker-compose exec postgres psql -U bashServiceAccount -d bagnsave_db_v2 -c "SELECT 1"

# Check Spring Boot logs
docker-compose logs backend | grep -i datasource
```

### Docker image build fails
```bash
# Rebuild without cache
docker-compose build --no-cache

# Check specific errors
docker build --no-cache -f backend/Dockerfile ./backend
```

### Port already in use
```bash
# Find what's using port 80
lsof -i :80

# Or change port in docker-compose.yml
# Change "80:80" to "8000:80" to use port 8000
```

## 📖 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS RDS Documentation](https://docs.aws.amazon.com/rds/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Nginx Proxy Setup](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)

## 📞 Getting Help

1. Check `TROUBLESHOOTING` section above
2. Review logs: `docker-compose logs -f`
3. See `AWS_DEPLOYMENT_GUIDE.md` for deployment-specific help
4. See `QUICK_START.md` for command reference

## 🎉 Next Steps

1. ✅ Test locally with Docker Compose
2. ✅ Set up AWS account and credentials
3. ✅ Choose deployment option
4. ✅ Run `./aws-deploy.sh full-setup us-east-1`
5. ✅ Set up database
6. ✅ Deploy and test
7. ✅ Configure domain and HTTPS
8. ✅ Set up monitoring

---

**Happy Deploying! 🚀**

For questions or issues, refer to the detailed guides in this directory.

