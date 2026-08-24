# AWS Deployment Guide for BagnSave

## Prerequisites
- AWS Account
- AWS CLI configured with credentials
- Docker and Docker Compose installed locally (for testing)

## Option 1: AWS ECS (Recommended for Beginners)

### Step 1: Create ECR Repositories
```bash
# Create repository for backend
aws ecr create-repository --repository-name bagnsave-backend --region us-east-1

# Create repository for frontend
aws ecr create-repository --repository-name bagnsave-frontend --region us-east-1

# Get login token and login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com
```

### Step 2: Build and Push Images to ECR
```bash
# Build backend
docker build -t bagnsave-backend:latest ./backend
docker tag bagnsave-backend:latest <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest
docker push <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest

# Build frontend
docker build -t bagnsave-frontend:latest ./frontend
docker tag bagnsave-frontend:latest <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
docker push <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
```

### Step 3: Set Up RDS PostgreSQL Database
1. Go to AWS RDS Console
2. Create new database:
   - Engine: PostgreSQL 17
   - DB Instance Class: db.t3.micro (free tier eligible)
   - Storage: 20 GB gp3
   - DB Name: bagnsave_db_v2
   - Username: bashServiceAccount
   - Password: (Create a strong password and save it!)
3. In Security Groups, allow inbound traffic on port 5432
4. Note the endpoint (e.g., bagnsave-db.xxxxxx.us-east-1.rds.amazonaws.com)

### Step 4: Create ECS Cluster
```bash
aws ecs create-cluster --cluster-name bagnsave-cluster --region us-east-1
```

### Step 5: Create Task Definitions
See `ecs-task-definition-backend.json` and `ecs-task-definition-frontend.json` in this directory

### Step 6: Create Services
Create services in the ECS cluster that run your tasks, expose ports, and handle load balancing

### Step 7: Configure Application Load Balancer (ALB)
1. Create ALB in EC2 Console
2. Create target groups for frontend (port 80) and backend (port 8080)
3. Create listener rules to route traffic

## Option 2: AWS Lightsail (Easiest Option)

### Step 1: Create a Lightsail Container Service
```bash
aws lightsail create-container-service --service-name bagnsave --power small --region us-east-1
```

### Step 2: Push Images to Lightsail
```bash
# Push backend
docker build -t bagnsave-backend:latest ./backend
aws lightsail push-container-image --region us-east-1 --service-name bagnsave --label bagnsave-backend --image bagnsave-backend:latest

# Push frontend
docker build -t bagnsave-frontend:latest ./frontend
aws lightsail push-container-image --region us-east-1 --service-name bagnsave --label bagnsave-frontend --image bagnsave-frontend:latest
```

### Step 3: Create Container Service Deployment (see containers.json)

## Option 3: AWS App Runner (Fastest)

Easiest option - just connect your GitHub repository and it auto-deploys!

### Step 1: Create App Runner Service for Backend
1. Go to AWS App Runner
2. Connect GitHub repo
3. Select repository and branch
4. Configure build settings (Dockerfile path: backend/Dockerfile)
5. Set environment variables with RDS endpoint

### Step 2: Create App Runner Service for Frontend
Same process but with frontend/Dockerfile

## Option 4: AWS Elastic Beanstalk

Multi-container Docker support with automatic scaling

### Step 1: Create Dockerrun.aws.json
```bash
# See Dockerrun.aws.json in this directory
```

### Step 2: Deploy
```bash
eb create bagnsave-env
eb deploy
```

## Database Migration Steps (All Options)

1. Connect to RDS instance using psql:
```bash
psql -h <RDS_ENDPOINT> -U bashServiceAccount -d bagnsave_db_v2
```

2. Run your database schema from the database/Working/createPostgreSQL.sql file

3. Update backend environment variables:
   - SPRING_DATASOURCE_URL: jdbc:postgresql://<RDS_ENDPOINT>:5432/bagnsave_db_v2
   - SPRING_DATASOURCE_USERNAME: bashServiceAccount
   - SPRING_DATASOURCE_PASSWORD: <your_password>

## Testing Locally with Docker Compose

Before deploying to AWS, test locally:

```bash
# Start all services (frontend, backend, postgres)
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop services
docker-compose down
```

Visit:
- Frontend: http://localhost
- Backend API: http://localhost:8080/api

## Environment Variables to Set in AWS

### Backend
- `SPRING_DATASOURCE_URL`: jdbc:postgresql://<RDS_ENDPOINT>:5432/bagnsave_db_v2
- `SPRING_DATASOURCE_USERNAME`: bashServiceAccount
- `SPRING_DATASOURCE_PASSWORD`: <secure_password>
- `SPRING_PROFILES_ACTIVE`: prod

### Frontend
- No special environment variables needed (API endpoint is configured in nginx)

## Cost Estimation (Monthly)

- RDS PostgreSQL db.t3.micro: ~$9-15
- ECS Fargate (2 vCPU, 4GB RAM): ~$30-50
- ALB: ~$16
- NAT Gateway: ~$32
- **Total: ~$90-110/month**

Or use Lightsail small ($3.50/month) or App Runner (pay per use, ~$1/month+ for minimal traffic)

## Next Steps

1. Set up AWS account and configure CLI
2. Test locally with `docker-compose up`
3. Push images to ECR
4. Set up RDS database
5. Choose deployment option (ECS, Lightsail, or App Runner)
6. Configure environment variables
7. Monitor and scale as needed

## Monitoring & Logging

- CloudWatch: Monitor container logs and metrics
- X-Ray: Trace requests through your application
- CloudTrail: Audit AWS API calls

## Security Best Practices

1. Use AWS Secrets Manager for database passwords
2. Enable VPC and security groups for database
3. Use HTTPS with ACM certificates
4. Enable CORS properly in backend
5. Keep base images updated
6. Scan container images for vulnerabilities

