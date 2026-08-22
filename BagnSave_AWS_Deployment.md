# BagnSave AWS Deployment Guide

## Architecture Overview

BagnSave runs on three AWS services that communicate with each other:

- **Database** — PostgreSQL on RDS (Relational Database Service). Stores all product, price, and store data. Only the backend can talk to it.
- **Backend** — Spring Boot app running as a Docker container on ECS Fargate. Connects to RDS and exposes a REST API at context path `/api`. All endpoints are therefore at `/api/api/...` (e.g. `/api/api/stores`).
- **Frontend** — React app compiled to static files, served by Nginx inside a Docker container on ECS Fargate. Served on port 80.
- **Load Balancer** — A single Application Load Balancer (ALB) is the public entry point. It routes `/api/*` requests to the backend and everything else to the frontend. This means the frontend can call `/api/api/stores` as a relative URL and the ALB handles forwarding.

```
User browser
     │
     ▼
Application Load Balancer (bagnsave-alb)
     ├── /api/*  ──▶  Backend container (Fargate, port 8080)
     │                        │
     │                        ▼
     │                  RDS PostgreSQL (port 5432)
     │
     └── /*      ──▶  Frontend container (Fargate, port 80)
                       Nginx serves static React files
```

### Why the frontend uses `/api/api/...`

The Spring Boot backend is configured with a context path of `/api`, so all its endpoints are prefixed with `/api`. When the ALB forwards a request from the frontend's `/api/stores` call, it hits the backend at `/api/stores`, but the backend's actual route is `/api/stores` relative to its context path — making the full path `/api/api/stores`. The frontend's `api.ts` file prepends `/api/api` to all fetch calls to account for this.

---

## Prerequisites

- AWS CLI installed and configured (`aws configure`)
- Docker installed (with buildx for multi-architecture builds)
- AWS account with IAM permissions for ECS, ECR, RDS, ELB, CloudFormation, EC2, and IAM

---

## Step 1 — Configure AWS CLI

```bash
aws configure
# Enter your Access Key ID
# Enter your Secret Access Key
# Region: us-east-1
# Output format: json
```

---

## Step 2 — Create the RDS PostgreSQL Database

```bash
aws rds create-db-instance \
  --db-instance-identifier bagnsave-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 17 \
  --master-username bashServiceAccount \
  --master-user-password BagNSaveSecure2026 \
  --allocated-storage 20 \
  --db-name bagnsave_db_v2 \
  --publicly-accessible
```

Wait a few minutes for the database to become available, then get its endpoint:

```bash
aws rds describe-db-instances \
  --db-instance-identifier bagnsave-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

Save this endpoint — you will need it in later steps.

Allow incoming connections on port 5432 from anywhere (required for the backend to connect):

```bash
VPC_ID=$(aws ec2 describe-vpcs --filters "Name=is-default,Values=true" --query 'Vpcs[0].VpcId' --output text)
SG_ID=$(aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$VPC_ID" "Name=group-name,Values=default" --query 'SecurityGroups[0].GroupId' --output text)

aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 5432 \
  --cidr 0.0.0.0/0
```

---

## Step 3 — Create ECR Repositories

Create container registries to store the Docker images:

```bash
aws ecr create-repository --repository-name bagnsave-backend --region us-east-1
aws ecr create-repository --repository-name bagnsave-frontend --region us-east-1
```

Note your AWS account ID from the output (e.g. `959039138777`). Your ECR base URL will be:
`<account-id>.dkr.ecr.us-east-1.amazonaws.com`

---

## Step 4 — Build and Push the Backend Docker Image

```bash
cd backend

# Build the Spring Boot jar
./mvnw clean package -DskipTests

# Build multi-architecture Docker image (required for Apple Silicon / ARM64 macs)
docker build --platform linux/amd64 -t bagnsave-backend .

# Log into ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag bagnsave-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest
```

---

## Step 5 — Build and Push the Frontend Docker Image

The frontend Dockerfile does two things:
1. Uses Node.js to compile the React app with Vite into static files
2. Copies those static files into an Nginx container that serves them on port 80

```bash
cd frontend

# Build multi-architecture Docker image
docker build --platform linux/amd64 -t bagnsave-frontend .

# Log into ECR (if not already logged in)
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag bagnsave-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
```

---

## Step 6 — Deploy with CloudFormation

From the root directory (where `cloudformation-template.json` is located), deploy the full stack. This creates the ECS cluster, task definitions, services, and ALB in one command.

```bash
# Get your network details
VPC_ID=$(aws ec2 describe-vpcs --filters "Name=is-default,Values=true" --query 'Vpcs[0].VpcId' --output text)
SG_ID=$(aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$VPC_ID" "Name=group-name,Values=default" --query 'SecurityGroups[0].GroupId' --output text)

# Get two subnets (the ALB requires at least two availability zones)
aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" --query 'Subnets[*].SubnetId' --output text
# Copy two subnet IDs from the output for SubnetOne and SubnetTwo below

aws cloudformation deploy \
  --template-file cloudformation-template.json \
  --stack-name bagnsave-stack \
  --parameter-overrides \
    VpcId=<YOUR-VPC-ID> \
    SubnetOne=<YOUR-FIRST-SUBNET-ID> \
    SubnetTwo=<YOUR-SECOND-SUBNET-ID> \
    SecurityGroupId=<YOUR-SECURITY-GROUP-ID> \
    DBEndpoint=<YOUR-RDS-ENDPOINT> \
    DBUsername=bashServiceAccount \
    DBPassword=BagNSaveSecure2026 \
    ECRBackendImage=<account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest \
    ECRFrontendImage=<account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest \
  --capabilities CAPABILITY_NAMED_IAM
```

Wait for the stack to complete (5–10 minutes), then get the ALB DNS name:

```bash
aws cloudformation describe-stacks \
  --stack-name bagnsave-stack \
  --query "Stacks[0].Outputs[?OutputKey=='LoadBalancerDNS'].OutputValue" \
  --output text
```

The app will be accessible at `http://<alb-dns-name>/select-stores`.

---

## Step 7 — Open Required Ports

Ensure the security group allows traffic on the necessary ports:

```bash
# Port 80 — public web traffic to the frontend
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

# Port 8080 — ALB health checks and traffic to the backend
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0
```

---

## Step 8 — Load the Database

Push your local database to RDS:

```bash
cd database/working

# Start the local database container
docker rm -f my-grocer-db
docker run -d --name my-grocer-db -p 5432:5432 grocer-db-pipeline

echo "Container is running. Pushing data to AWS RDS..."

# Dump from local container and pipe directly into RDS
docker exec -t my-grocer-db pg_dump -U bashServiceAccount -d bagnsave_db_v2 --data-only \
  | docker run --rm -i postgres:18.4 psql \
    "postgresql://bashServiceAccount:BagNSaveSecure2026@<YOUR-RDS-ENDPOINT>:5432/bagnsave_db_v2?sslmode=require"
```

---

## Updating After Code Changes

### Update the backend

```bash
cd backend
./mvnw clean package -DskipTests
docker build --platform linux/amd64 -t bagnsave-backend .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
docker tag bagnsave-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest
aws ecs update-service --cluster bagnsave-cluster --service bagnsave-backend-service --force-new-deployment
```

### Update the frontend

```bash
cd frontend
docker build --platform linux/amd64 -t bagnsave-frontend .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
docker tag bagnsave-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-frontend:latest
aws ecs update-service --cluster bagnsave-cluster --service bagnsave-frontend-service --force-new-deployment
```

---

## Troubleshooting

### Check if services are running

```bash
aws ecs describe-services \
  --cluster bagnsave-cluster \
  --services bagnsave-backend-service bagnsave-frontend-service \
  --query 'services[*].[serviceName,runningCount,desiredCount]' \
  --output table
```

You want `runningCount` to equal `desiredCount` (both should be `1`) for each service.

### Check backend logs

```bash
aws logs get-log-events \
  --log-group-name /ecs/bagnsave-backend \
  --log-stream-name $(aws logs describe-log-streams \
    --log-group-name /ecs/bagnsave-backend \
    --order-by LastEventTime \
    --descending \
    --limit 1 \
    --query 'logStreams[0].logStreamName' \
    --output text) \
  --limit 30 \
  --query 'events[*].message' \
  --output text
```

### Check ALB target health

```bash
# Get the backend target group ARN
TG_ARN=$(aws elbv2 describe-target-groups \
  --names bagnsave-backend-tg \
  --query 'TargetGroups[0].TargetGroupArn' \
  --output text)

aws elbv2 describe-target-health \
  --target-group-arn $TG_ARN \
  --query 'TargetHealthDescriptions[*].[Target.Id,TargetHealth.State,TargetHealth.Description]' \
  --output table
```

Targets should show `healthy`. Common states and causes:

| State | Likely cause |
|---|---|
| `unhealthy` (404) | Health check path is wrong — backend context path is `/api/api/...` not `/api/...` |
| `unhealthy` (timeout) | Port 8080 not open in the security group, or backend task has crashed |
| `draining` | Old task is shutting down — a new one should register shortly |

### Fix the backend task definition if it has the wrong DB endpoint

```bash
aws ecs register-task-definition \
  --family bagnsave-backend \
  --network-mode awsvpc \
  --requires-compatibilities FARGATE \
  --cpu 256 \
  --memory 512 \
  --execution-role-arn $(aws ecs describe-task-definition --task-definition bagnsave-backend --query 'taskDefinition.executionRoleArn' --output text) \
  --container-definitions '[
    {
      "name": "backend",
      "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/bagnsave-backend:latest",
      "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
      "environment": [
        {"name": "SPRING_DATASOURCE_URL", "value": "jdbc:postgresql://<YOUR-RDS-ENDPOINT>:5432/bagnsave_db_v2"},
        {"name": "SPRING_DATASOURCE_USERNAME", "value": "bashServiceAccount"},
        {"name": "SPRING_DATASOURCE_PASSWORD", "value": "BagNSaveSecure2026"}
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/bagnsave-backend",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]'

aws ecs update-service \
  --cluster bagnsave-cluster \
  --service bagnsave-backend-service \
  --task-definition bagnsave-backend \
  --force-new-deployment
```

---

## Key Codebase Notes

- **Multi-architecture Docker builds** — use `--platform linux/amd64` on all `docker build` commands. Apple Silicon (ARM64) Macs produce ARM images by default, which fail to run on Fargate's x86 infrastructure.
- **Spring Boot context path** — the backend is configured with `server.servlet.context-path=/api`, so all endpoints live under `/api/api/...`. The frontend's `src/api.ts` file uses `/api/api` as the base URL for all `apiFetch` calls.
- **Nginx config** — `frontend/nginx.conf` contains only the React Router SPA fallback (`try_files $uri $uri/ /index.html`). No proxy rules are needed because the ALB handles routing to the backend.
- **Spring Boot Hibernate** — set to `update` mode, which automatically creates any missing tables on startup.
- **Spring Boot profile** — runs with the `prod` profile in production, which reads database credentials from environment variables injected by ECS.
