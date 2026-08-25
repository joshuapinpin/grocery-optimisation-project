#!/bin/bash

# AWS Deployment Helper Script
# This script helps with common AWS deployment tasks

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     BagnSave AWS Deployment Helper Script         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"

# Check prerequisites
check_prerequisites() {
    echo -e "\n${YELLOW}Checking prerequisites...${NC}"

    if ! command -v aws &> /dev/null; then
        echo -e "${RED}✗ AWS CLI is not installed${NC}"
        echo "  Install from: https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html"
        exit 1
    fi
    echo -e "${GREEN}✓ AWS CLI is installed${NC}"

    if ! command -v docker &> /dev/null; then
        echo -e "${RED}✗ Docker is not installed${NC}"
        echo "  Install from: https://www.docker.com/products/docker-desktop"
        exit 1
    fi
    echo -e "${GREEN}✓ Docker is installed${NC}"

    # Check AWS credentials
    if ! aws sts get-caller-identity > /dev/null 2>&1; then
        echo -e "${RED}✗ AWS credentials are not configured${NC}"
        echo "  Run: aws configure"
        exit 1
    fi
    echo -e "${GREEN}✓ AWS credentials are configured${NC}"
}

# Get AWS account ID
get_account_id() {
    aws sts get-caller-identity --query Account --output text
}

# Create ECR repositories
create_ecr_repos() {
    echo -e "\n${YELLOW}Creating ECR repositories...${NC}"

    AWS_ACCOUNT_ID=$(get_account_id)
    AWS_REGION=${1:-us-east-1}

    echo -e "${BLUE}AWS Account ID: $AWS_ACCOUNT_ID${NC}"
    echo -e "${BLUE}AWS Region: $AWS_REGION${NC}"

    # Create backend repo
    if aws ecr describe-repositories --repository-names bagnsave-backend --region $AWS_REGION &> /dev/null; then
        echo -e "${YELLOW}✓ Backend repository already exists${NC}"
    else
        aws ecr create-repository \
            --repository-name bagnsave-backend \
            --region $AWS_REGION
        echo -e "${GREEN}✓ Backend repository created${NC}"
    fi

    # Create frontend repo
    if aws ecr describe-repositories --repository-names bagnsave-frontend --region $AWS_REGION &> /dev/null; then
        echo -e "${YELLOW}✓ Frontend repository already exists${NC}"
    else
        aws ecr create-repository \
            --repository-name bagnsave-frontend \
            --region $AWS_REGION
        echo -e "${GREEN}✓ Frontend repository created${NC}"
    fi

    echo -e "${BLUE}ECR Repositories:${NC}"
    echo -e "  Backend:  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/bagnsave-backend"
    echo -e "  Frontend: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/bagnsave-frontend"
}

# Build and push images
build_and_push() {
    echo -e "\n${YELLOW}Building and pushing Docker images...${NC}"

    AWS_ACCOUNT_ID=$(get_account_id)
    AWS_REGION=${1:-us-east-1}

    # Login to ECR
    echo -e "${BLUE}Logging into ECR...${NC}"
    aws ecr get-login-password --region $AWS_REGION | \
        docker login --username AWS --password-stdin \
        $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

    # Build backend
    echo -e "${BLUE}Building backend image...${NC}"
    docker build -t bagnsave-backend:latest ./backend

    echo -e "${BLUE}Tagging backend image...${NC}"
    docker tag bagnsave-backend:latest \
        $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/bagnsave-backend:latest

    echo -e "${BLUE}Pushing backend image...${NC}"
    docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/bagnsave-backend:latest
    echo -e "${GREEN}✓ Backend image pushed${NC}"

    # Build frontend
    echo -e "${BLUE}Building frontend image...${NC}"
    docker build -t bagnsave-frontend:latest ./frontend

    echo -e "${BLUE}Tagging frontend image...${NC}"
    docker tag bagnsave-frontend:latest \
        $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/bagnsave-frontend:latest

    echo -e "${BLUE}Pushing frontend image...${NC}"
    docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/bagnsave-frontend:latest
    echo -e "${GREEN}✓ Frontend image pushed${NC}"
}

# Create ECS cluster
create_ecs_cluster() {
    echo -e "\n${YELLOW}Creating ECS cluster...${NC}"

    AWS_REGION=${1:-us-east-1}

    if aws ecs describe-clusters --clusters bagnsave-cluster --region $AWS_REGION 2>/dev/null | grep -q "arn"; then
        echo -e "${YELLOW}✓ ECS cluster already exists${NC}"
    else
        aws ecs create-cluster \
            --cluster-name bagnsave-cluster \
            --region $AWS_REGION
        echo -e "${GREEN}✓ ECS cluster created${NC}"
    fi
}

# Show status
show_status() {
    echo -e "\n${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}Deployment Status${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"

    AWS_REGION=${1:-us-east-1}

    echo -e "\n${YELLOW}ECR Repositories:${NC}"
    aws ecr describe-repositories --region $AWS_REGION --query 'repositories[?contains(repositoryName, `bagnsave`)].{Name:repositoryName, URI:repositoryUri}' --output table

    echo -e "\n${YELLOW}ECS Clusters:${NC}"
    aws ecs list-clusters --region $AWS_REGION --output table

    echo -e "\n${YELLOW}RDS Instances:${NC}"
    aws rds describe-db-instances --db-instance-identifier bagnsave-db --region $AWS_REGION --query 'DBInstances[0].{Identifier:DBInstanceIdentifier, Status:DBInstanceStatus, Engine:Engine, Endpoint:Endpoint.Address}' --output table 2>/dev/null || echo "No RDS instance found"
}

# Clean up
cleanup() {
    echo -e "\n${RED}WARNING: This will delete all resources${NC}"
    read -p "Are you sure? (yes/no): " -r

    if [[ $REPLY =~ ^[Yy]es$ ]]; then
        AWS_REGION=${1:-us-east-1}

        echo -e "${YELLOW}Deleting ECR repositories...${NC}"
        aws ecr delete-repository --repository-name bagnsave-backend --force --region $AWS_REGION 2>/dev/null || true
        aws ecr delete-repository --repository-name bagnsave-frontend --force --region $AWS_REGION 2>/dev/null || true

        echo -e "${YELLOW}Deleting ECS cluster...${NC}"
        aws ecs delete-cluster --cluster bagnsave-cluster --force --region $AWS_REGION 2>/dev/null || true

        echo -e "${GREEN}✓ Cleanup complete${NC}"
    else
        echo -e "${YELLOW}Cleanup cancelled${NC}"
    fi
}

# Main menu
show_menu() {
    echo -e "\n${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "Select an option:"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "1. Check prerequisites"
    echo -e "2. Create ECR repositories"
    echo -e "3. Build and push Docker images"
    echo -e "4. Create ECS cluster"
    echo -e "5. Show deployment status"
    echo -e "6. Full setup (1-4)"
    echo -e "7. Cleanup resources"
    echo -e "0. Exit"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
}

# Parse region
AWS_REGION="${AWS_REGION:-us-east-1}"
if [ ! -z "$2" ]; then
    AWS_REGION="$2"
fi

# Main loop
if [ -z "$1" ]; then
    while true; do
        show_menu
        read -p "Enter your choice [0-7]: " choice

        case $choice in
            1) check_prerequisites ;;
            2) create_ecr_repos $AWS_REGION ;;
            3) build_and_push $AWS_REGION ;;
            4) create_ecs_cluster $AWS_REGION ;;
            5) show_status $AWS_REGION ;;
            6)
                check_prerequisites
                create_ecr_repos $AWS_REGION
                build_and_push $AWS_REGION
                create_ecs_cluster $AWS_REGION
                show_status $AWS_REGION
                ;;
            7) cleanup $AWS_REGION ;;
            0)
                echo -e "${GREEN}Goodbye!${NC}"
                exit 0
                ;;
            *) echo -e "${RED}Invalid option. Please try again.${NC}" ;;
        esac
    done
else
    case $1 in
        check) check_prerequisites ;;
        create-repos) create_ecr_repos $AWS_REGION ;;
        build-push) build_and_push $AWS_REGION ;;
        create-cluster) create_ecs_cluster $AWS_REGION ;;
        status) show_status $AWS_REGION ;;
        full-setup)
            check_prerequisites
            create_ecr_repos $AWS_REGION
            build_and_push $AWS_REGION
            create_ecs_cluster $AWS_REGION
            show_status $AWS_REGION
            ;;
        cleanup) cleanup $AWS_REGION ;;
        *)
            echo "Usage: $0 [check|create-repos|build-push|create-cluster|status|full-setup|cleanup] [region]"
            echo ""
            echo "Examples:"
            echo "  $0 check                    # Check prerequisites"
            echo "  $0 full-setup us-east-1     # Complete setup in us-east-1"
            echo "  $0 status eu-west-1         # Check status in eu-west-1"
            exit 1
            ;;
    esac
fi

