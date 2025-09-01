#!/bin/bash

# Book Review Platform Deployment Script
# This script handles deployment with existing resource checks

set -e

echo "🚀 Starting Book Review Platform Deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    print_error "AWS CLI is not configured. Please run 'aws configure' first."
    exit 1
fi

# Set AWS region
export AWS_DEFAULT_REGION=us-east-1
print_status "Using AWS region: $AWS_DEFAULT_REGION"

# Change to infrastructure directory
cd infra/us-east-1-minimal

# Check for existing resources
print_status "Checking for existing resources..."

# Check if load balancer exists
if aws elbv2 describe-load-balancers --names "book-review-minimal" &> /dev/null; then
    print_warning "Load balancer 'book-review-minimal' already exists"
    EXISTING_LB=true
else
    print_status "No existing load balancer found"
    EXISTING_LB=false
fi

# Check if target groups exist
if aws elbv2 describe-target-groups --names "book-review-backend" &> /dev/null; then
    print_warning "Target group 'book-review-backend' already exists"
    EXISTING_BACKEND_TG=true
else
    print_status "No existing backend target group found"
    EXISTING_BACKEND_TG=false
fi

if aws elbv2 describe-target-groups --names "book-review-frontend" &> /dev/null; then
    print_warning "Target group 'book-review-frontend' already exists"
    EXISTING_FRONTEND_TG=true
else
    print_status "No existing frontend target group found"
    EXISTING_FRONTEND_TG=false
fi

# Check if IAM roles exist
if aws iam get-role --role-name "book-review-ecs-execution-role" &> /dev/null; then
    print_warning "IAM role 'book-review-ecs-execution-role' already exists"
    EXISTING_EXECUTION_ROLE=true
else
    print_status "No existing execution role found"
    EXISTING_EXECUTION_ROLE=false
fi

if aws iam get-role --role-name "book-review-ecs-task-role" &> /dev/null; then
    print_warning "IAM role 'book-review-ecs-task-role' already exists"
    EXISTING_TASK_ROLE=true
else
    print_status "No existing task role found"
    EXISTING_TASK_ROLE=false
fi

# Check if CloudWatch log groups exist
if aws logs describe-log-groups --log-group-name-prefix "/ecs/book-review-backend" --query 'logGroups[?logGroupName==`/ecs/book-review-backend`]' --output text | grep -q "/ecs/book-review-backend"; then
    print_warning "CloudWatch log group '/ecs/book-review-backend' already exists"
    EXISTING_BACKEND_LOGS=true
else
    print_status "No existing backend log group found"
    EXISTING_BACKEND_LOGS=false
fi

if aws logs describe-log-groups --log-group-name-prefix "/ecs/book-review-frontend" --query 'logGroups[?logGroupName==`/ecs/book-review-frontend`]' --output text | grep -q "/ecs/book-review-frontend"; then
    print_warning "CloudWatch log group '/ecs/book-review-frontend' already exists"
    EXISTING_FRONTEND_LOGS=true
else
    print_status "No existing frontend log group found"
    EXISTING_FRONTEND_LOGS=false
fi

print_status "Resource check complete. Proceeding with Terraform deployment..."

# Initialize Terraform
print_status "Initializing Terraform..."
terraform init

# Plan deployment
print_status "Planning Terraform deployment..."
terraform plan -out=tfplan

# Apply deployment
print_status "Applying Terraform deployment..."
terraform apply -auto-approve tfplan

# Get outputs
print_status "Deployment completed successfully! 🎉"
echo ""
echo "📊 Deployment Information:"
echo "=========================="
echo "Backend URL: $(terraform output -raw backend_url)"
echo "Frontend URL: $(terraform output -raw frontend_url)"
echo "Load Balancer DNS: $(terraform output -raw alb_dns_name)"
echo ""

print_status "Deployment script completed successfully!"
