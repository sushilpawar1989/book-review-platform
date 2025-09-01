#!/bin/bash

echo "🚀 AWS Deployment Script for Book Review Platform"
echo "================================================="

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI not found. Please install it first:"
    echo "   https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if Terraform is installed
if ! command -v terraform &> /dev/null; then
    echo "❌ Terraform not found. Please install it first:"
    echo "   https://learn.hashicorp.com/tutorials/terraform/install-cli"
    exit 1
fi

# Check AWS credentials
echo "🔍 Checking AWS credentials..."
aws sts get-caller-identity > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ AWS credentials not configured. Please run:"
    echo "   aws configure"
    echo "   Or set environment variables:"
    echo "   export AWS_ACCESS_KEY_ID=your-key"
    echo "   export AWS_SECRET_ACCESS_KEY=your-secret"
    echo "   export AWS_DEFAULT_REGION=us-east-1"
    exit 1
fi

echo "✅ AWS credentials configured"

# Get user inputs
read -p "🔑 Enter JWT Secret (or press Enter for default): " JWT_SECRET
if [ -z "$JWT_SECRET" ]; then
    JWT_SECRET="YjM2N2Q4ZWZhMTJjNGU5OGI3ZjNkMmE4YzVlNjRiOWQ5ZmE3YjNlOGM0ZDZhOWIyZjhlNzMxNGM5ZGI2YWY4ZA=="
fi

read -p "🤖 Enter OpenAI API Key (optional, press Enter to skip): " OPENAI_KEY

read -p "🌍 Enter AWS Region (default: us-east-1): " AWS_REGION
if [ -z "$AWS_REGION" ]; then
    AWS_REGION="us-east-1"
fi

read -p "📝 Enter Environment (dev/staging/prod, default: dev): " ENVIRONMENT
if [ -z "$ENVIRONMENT" ]; then
    ENVIRONMENT="dev"
fi

# Navigate to Terraform directory
cd infra/backend

echo "🏗️ Initializing Terraform..."
terraform init

echo "📋 Creating Terraform plan..."
terraform plan \
    -var="jwt_secret=$JWT_SECRET" \
    -var="openai_api_key=$OPENAI_KEY" \
    -var="aws_region=$AWS_REGION" \
    -var="environment=$ENVIRONMENT" \
    -out=tfplan

echo ""
echo "📊 Terraform Plan Summary:"
echo "=========================="
echo "🌍 Region: $AWS_REGION"
echo "📝 Environment: $ENVIRONMENT"
echo "🔑 JWT Secret: [CONFIGURED]"
echo "🤖 OpenAI Key: $([ -n "$OPENAI_KEY" ] && echo '[CONFIGURED]' || echo '[NOT SET]')"
echo ""

read -p "🚀 Do you want to apply this plan? (y/N): " CONFIRM
if [[ $CONFIRM =~ ^[Yy]$ ]]; then
    echo "🚀 Applying Terraform configuration..."
    terraform apply tfplan
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "🎉 Deployment completed successfully!"
        echo ""
        echo "📊 Deployment Information:"
        echo "========================="
        terraform output
        echo ""
        echo "🔗 Next Steps:"
        echo "1. Note the Load Balancer URL above"
        echo "2. Update your frontend VITE_API_BASE_URL to point to the backend"
        echo "3. Deploy your frontend to S3/CloudFront or update the container image"
        echo ""
        echo "📋 Useful Commands:"
        echo "- View resources: terraform show"
        echo "- Destroy resources: terraform destroy"
        echo "- Update deployment: terraform apply"
    else
        echo "❌ Deployment failed. Check the error messages above."
        exit 1
    fi
else
    echo "⏹️ Deployment cancelled."
    rm -f tfplan
fi

cd ../..
echo "✅ Done!"
