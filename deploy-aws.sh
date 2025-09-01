#!/bin/bash

# AWS Deployment Script for Book Review Platform
# This script builds and pushes Docker images to ECR, then deploys infrastructure

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="book-review-platform"
ENVIRONMENT="dev"
AWS_REGION="us-east-1"
BACKEND_IMAGE_TAG="latest"
FRONTEND_IMAGE_TAG="latest"

echo -e "${BLUE}🚀 Starting AWS deployment for ${PROJECT_NAME}...${NC}"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI is not installed. Please install it first.${NC}"
    exit 1
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Check if Terraform is installed
if ! command -v terraform &> /dev/null; then
    echo -e "${RED}❌ Terraform is not installed. Please install it first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Prerequisites check passed${NC}"

# Navigate to project root
cd "$(dirname "$0")"

# Step 1: Initialize Terraform
echo -e "${BLUE}📋 Initializing Terraform...${NC}"
cd infra/backend
terraform init

# Step 2: Get ECR repository URLs
echo -e "${BLUE}🔍 Getting ECR repository URLs...${NC}"
BACKEND_ECR_URL=$(terraform output -raw ecr_backend_repository 2>/dev/null || echo "")
FRONTEND_ECR_URL=$(terraform output -raw ecr_frontend_repository 2>/dev/null || echo "")

if [ -z "$BACKEND_ECR_URL" ] || [ -z "$FRONTEND_ECR_URL" ]; then
    echo -e "${YELLOW}⚠️  ECR repositories not found. Creating infrastructure first...${NC}"
    
    # Plan and apply infrastructure
    echo -e "${BLUE}📋 Planning Terraform changes...${NC}"
    terraform plan -out=tfplan
    
    echo -e "${BLUE}🚀 Applying Terraform changes...${NC}"
    terraform apply tfplan
    
    # Get ECR URLs after creation
    BACKEND_ECR_URL=$(terraform output -raw ecr_backend_repository)
    FRONTEND_ECR_URL=$(terraform output -raw ecr_frontend_repository)
    
    echo -e "${GREEN}✅ Infrastructure created successfully${NC}"
fi

echo -e "${GREEN}📦 Backend ECR: ${BACKEND_ECR_URL}${NC}"
echo -e "${GREEN}📦 Frontend ECR: ${FRONTEND_ECR_URL}${NC}"

# Step 3: Login to ECR
echo -e "${BLUE}🔐 Logging into ECR...${NC}"
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $BACKEND_ECR_URL
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $FRONTEND_ECR_URL

# Step 4: Build and push Backend image
echo -e "${BLUE}🏗️  Building Backend Docker image...${NC}"
cd ../../backend
docker build -t $PROJECT_NAME-backend:$BACKEND_IMAGE_TAG .

echo -e "${BLUE}🏷️  Tagging Backend image for ECR...${NC}"
docker tag $PROJECT_NAME-backend:$BACKEND_IMAGE_TAG $BACKEND_ECR_URL:$BACKEND_IMAGE_TAG

echo -e "${BLUE}📤 Pushing Backend image to ECR...${NC}"
docker push $BACKEND_ECR_URL:$BACKEND_IMAGE_TAG

echo -e "${GREEN}✅ Backend image pushed successfully${NC}"

# Step 5: Build and push Frontend image
echo -e "${BLUE}🏗️  Building Frontend Docker image...${NC}"
cd ../frontend
docker build -t $PROJECT_NAME-frontend:$FRONTEND_IMAGE_TAG .

echo -e "${BLUE}🏷️  Tagging Frontend image for ECR...${NC}"
docker tag $PROJECT_NAME-frontend:$FRONTEND_IMAGE_TAG $FRONTEND_ECR_URL:$FRONTEND_IMAGE_TAG

echo -e "${BLUE}📤 Pushing Frontend image to ECR...${NC}"
docker push $FRONTEND_ECR_URL:$FRONTEND_IMAGE_TAG

echo -e "${GREEN}✅ Frontend image pushed successfully${NC}"

# Step 6: Update ECS services to use new images
echo -e "${BLUE}🔄 Updating ECS services...${NC}"
cd ../infra/backend

# Force new deployment by updating task definition
echo -e "${BLUE}📋 Planning ECS update...${NC}"
terraform plan -out=tfplan

echo -e "${BLUE}🚀 Applying ECS update...${NC}"
terraform apply tfplan

# Step 7: Wait for services to be stable
echo -e "${BLUE}⏳ Waiting for ECS services to be stable...${NC}"
CLUSTER_NAME=$(terraform output -raw ecs_cluster_name)
BACKEND_SERVICE=$(terraform output -raw backend_service_name)
FRONTEND_SERVICE=$(terraform output -raw frontend_service_name)

echo -e "${BLUE}🔄 Waiting for Backend service to be stable...${NC}"
aws ecs wait services-stable \
    --cluster $CLUSTER_NAME \
    --services $BACKEND_SERVICE \
    --region $AWS_REGION

echo -e "${BLUE}🔄 Waiting for Frontend service to be stable...${NC}"
aws ecs wait services-stable \
    --cluster $CLUSTER_NAME \
    --services $FRONTEND_SERVICE \
    --region $AWS_REGION

# Step 8: Get deployment URLs
echo -e "${BLUE}🔍 Getting deployment URLs...${NC}"
BACKEND_URL=$(terraform output -raw backend_url)
FRONTEND_URL=$(terraform output -raw frontend_url)
ALB_DNS=$(terraform output -raw alb_dns_name)

echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo -e "${GREEN}📱 Frontend URL: ${FRONTEND_URL}${NC}"
echo -e "${GREEN}🔧 Backend URL: ${BACKEND_URL}${NC}"
echo -e "${GREEN}🌐 ALB DNS: ${ALB_DNS}${NC}"

# Step 9: Health check
echo -e "${BLUE}🏥 Performing health checks...${NC}"

# Check backend health
echo -e "${BLUE}🔍 Checking Backend health...${NC}"
if curl -f -s "${BACKEND_URL}/actuator/health" > /dev/null; then
    echo -e "${GREEN}✅ Backend is healthy${NC}"
else
    echo -e "${RED}❌ Backend health check failed${NC}"
fi

# Check frontend health
echo -e "${BLUE}🔍 Checking Frontend health...${NC}"
if curl -f -s "${FRONTEND_URL}" > /dev/null; then
    echo -e "${GREEN}✅ Frontend is healthy${NC}"
else
    echo -e "${RED}❌ Frontend health check failed${NC}"
fi

echo -e "${GREEN}🎊 All done! Your Book Review Platform is now running on AWS!${NC}"
