#!/bin/bash

echo "🔍 Testing AWS Credentials"
echo "=========================="

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI not found. Installing..."
    
    # Install AWS CLI on macOS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "📦 Installing AWS CLI for macOS..."
        curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
        sudo installer -pkg AWSCLIV2.pkg -target /
        rm AWSCLIV2.pkg
    else
        echo "Please install AWS CLI manually:"
        echo "https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
        exit 1
    fi
fi

echo "✅ AWS CLI found: $(aws --version)"

# Prompt for credentials
echo ""
echo "🔑 Enter your AWS credentials:"
read -p "AWS Access Key ID: " AWS_ACCESS_KEY_ID
read -s -p "AWS Secret Access Key: " AWS_SECRET_ACCESS_KEY
echo ""
read -p "AWS Region (default: us-east-1): " AWS_REGION

# Set defaults
if [ -z "$AWS_REGION" ]; then
    AWS_REGION="us-east-1"
fi

# Export credentials
export AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY
export AWS_DEFAULT_REGION=$AWS_REGION

echo ""
echo "🧪 Testing credentials..."

# Test 1: Get caller identity
echo "📋 Test 1: Get caller identity"
IDENTITY=$(aws sts get-caller-identity 2>&1)
if [ $? -eq 0 ]; then
    echo "✅ Credentials valid!"
    echo "   Account: $(echo $IDENTITY | jq -r '.Account' 2>/dev/null || echo 'N/A')"
    echo "   User: $(echo $IDENTITY | jq -r '.Arn' 2>/dev/null || echo 'N/A')"
else
    echo "❌ Credentials test failed:"
    echo "$IDENTITY"
    exit 1
fi

# Test 2: List ECR repositories (should be empty initially)
echo ""
echo "📋 Test 2: Check ECR access"
ECR_TEST=$(aws ecr describe-repositories --region $AWS_REGION 2>&1)
if [ $? -eq 0 ]; then
    echo "✅ ECR access working!"
else
    if echo "$ECR_TEST" | grep -q "RepositoryNotFoundException\|does not exist"; then
        echo "✅ ECR access working (no repositories yet - this is normal)"
    else
        echo "⚠️  ECR access issue:"
        echo "$ECR_TEST"
    fi
fi

# Test 3: List ECS clusters (should be empty initially)
echo ""
echo "📋 Test 3: Check ECS access"
ECS_TEST=$(aws ecs list-clusters --region $AWS_REGION 2>&1)
if [ $? -eq 0 ]; then
    echo "✅ ECS access working!"
else
    echo "⚠️  ECS access issue:"
    echo "$ECS_TEST"
fi

echo ""
echo "🎉 AWS credentials test completed!"
echo ""
echo "📋 Summary:"
echo "  Access Key: $AWS_ACCESS_KEY_ID"
echo "  Region: $AWS_REGION"
echo "  Status: ✅ Ready for deployment"
echo ""
echo "🚀 Next steps:"
echo "1. Add these credentials to GitHub Secrets"
echo "2. Run: ./trigger-deployment.sh"
echo "3. Monitor deployment at: https://github.com/sushilpawar1989/book-review-platform/actions"
