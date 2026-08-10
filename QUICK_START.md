# BagnSave Docker & AWS Quick Start

## 1. Test Locally with Docker Compose

```bash
# Navigate to project root
cd /Users/nickkho/Desktop/BagnSave

# Make build script executable
chmod +x build-docker.sh

# Build Docker images
./build-docker.sh

# Start all services (frontend, backend, database)
docker-compose up -d

# View logs
docker-compose logs -f

# Test the application
# Frontend: http://localhost
# Backend API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/api/swagger-ui.html

# Stop all services
docker-compose down
```

## 2. Deploy to AWS (Choose One Option)

### Option A: AWS Lightsail (Easiest - Recommended for beginners)

**Cost: ~$3.50/month for small container service**

```bash
# 1. Create Lightsail container service
aws lightsail create-container-service \
  --service-name bagnsave \
  --power small \
  --region us-east-1

# 2. Build and push images (from project root)
docker build -t bagnsave-backend:latest ./backend
aws lightsail push-container-image \
  --region us-east-1 \
  --service-name bagnsave \
  --label bagnsave-backend \
  --image bagnsave-backend:latest

docker build -t bagnsave-frontend:latest ./frontend
aws lightsail push-container-image \
  --region us-east-1 \
  --service-name bagnsave \
  --label bagnsave-frontend \
  --image bagnsave-frontend:latest

# 3. Create RDS Database (or use managed database in Lightsail)
# Follow AWS RDS Console instructions

# 4. Update environment variables in lightsail-containers.json with your RDS endpoint

# 5. Deploy containers
aws lightsail create-container-service-deployment \
  --service-name bagnsave \
  --cli-input-json file://lightsail-containers.json \
  --region us-east-1

# 6. Get the public endpoint
aws lightsail get-container-services \
  --service-name bagnsave \
  --region us-east-1
```

### Option B: AWS ECS Fargate (Production-grade - Recommended for scale)

**Cost: ~$30-50/month**

```bash
# 1. Create ECR repositories
aws ecr create-repository \
  --repository-name bagnsave-backend \
  --region us-east-1

aws ecr create-repository \
  --repository-name bagnsave-frontend \
  --region us-east-1

# 2. Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# 3. Build and push images
docker build -t bagnsave-backend:latest ./backend
docker tag bagnsave-backend:latest \
  <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest
docker push \
  <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest

docker build -t bagnsave-frontend:latest ./frontend
docker tag bagnsave-frontend:latest \
  <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
docker push \
  <YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest

# 4. Deploy CloudFormation stack
aws cloudformation create-stack \
  --stack-name bagnsave-stack \
  --template-body file://cloudformation-template.json \
  --parameters \
    ParameterKey=DBPassword,ParameterValue=<STRONG_PASSWORD> \
    ParameterKey=ECRBackendImage,ParameterValue=<YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest \
    ParameterKey=ECRFrontendImage,ParameterValue=<YOUR_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest \
  --capabilities CAPABILITY_IAM \
  --region us-east-1

# 5. Register task definitions
aws ecs register-task-definition \
  --cli-input-json file://ecs-task-definition-backend.json \
  --region us-east-1

aws ecs register-task-definition \
  --cli-input-json file://ecs-task-definition-frontend.json \
  --region us-east-1
```

### Option C: AWS App Runner (Automatic CI/CD from GitHub)

**Cost: ~$1-5/month for minimal traffic**

1. Connect your GitHub repository to AWS App Runner
2. For backend service:
   - Build command: `mvn clean package`
   - Start command: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
3. For frontend service:
   - Build command: `npm ci && npm run build`
   - Start command: `npm run preview`

## 3. Database Setup

```bash
# Get RDS endpoint
RDS_ENDPOINT=$(aws rds describe-db-instances \
  --db-instance-identifier bagnsave-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text \
  --region us-east-1)

# Connect to database
psql -h $RDS_ENDPOINT -U bashServiceAccount -d bagnsave_db_v2

# Run SQL schema (from database/Working/createPostgreSQL.sql)
# Copy the content and paste into psql prompt
```

## 4. Monitor Your Application

```bash
# View CloudWatch logs
aws logs tail /ecs/bagnsave-backend --follow
aws logs tail /ecs/bagnsave-frontend --follow

# View container status
aws ecs list-services --cluster bagnsave-cluster

# View task status
aws ecs list-tasks --cluster bagnsave-cluster --service-name bagnsave-backend
```

## 5. Environment Variables to Set

### Backend
- `SPRING_DATASOURCE_URL`: jdbc:postgresql://<RDS_ENDPOINT>:5432/bagnsave_db_v2
- `SPRING_DATASOURCE_USERNAME`: bashServiceAccount
- `SPRING_DATASOURCE_PASSWORD`: <your_secure_password>
- `SPRING_PROFILES_ACTIVE`: prod

### Frontend
- Configure backend URL in nginx.conf (or environment variable)

## 6. Troubleshooting

### Container won't start
```bash
# Check logs
docker-compose logs backend
docker-compose logs frontend

# Verify database connectivity
docker-compose exec backend curl http://localhost:8080/api/swagger-ui.html
```

### Frontend can't reach backend
- Ensure backend is running on http://backend:8080 (in docker-compose)
- Check nginx.conf proxy settings
- Verify security groups allow traffic

### Database connection fails
```bash
# Test database connection
psql -h $RDS_ENDPOINT -U bashServiceAccount -d bagnsave_db_v2 -c "SELECT 1"
```

## Important Notes

1. **Update `application-prod.properties`** with production database settings before building
2. **Use AWS Secrets Manager** for sensitive credentials in production
3. **Enable HTTPS** with ACM certificates
4. **Set up auto-scaling policies** for production workloads
5. **Configure backup and disaster recovery** strategies
6. **Monitor costs** with AWS Billing Dashboard

## Next Steps

1. ✅ Test locally with docker-compose
2. ✅ Set up AWS account and configure CLI
3. ✅ Choose deployment option (Lightsail/ECS/App Runner)
4. ✅ Deploy and monitor
5. ✅ Set up CI/CD pipeline with GitHub Actions

See AWS_DEPLOYMENT_GUIDE.md for detailed information on each option.

