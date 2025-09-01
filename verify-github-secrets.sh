#!/bin/bash

# verify-github-secrets.sh
# Script to verify GitHub repository secrets are configured for AWS deployment

echo "🔍 Verifying GitHub Repository Secrets Configuration"
echo "=================================================="

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "❌ Error: Not in a git repository"
    exit 1
fi

# Get repository information
REPO_URL=$(git config --get remote.origin.url)
if [[ $REPO_URL == *"github.com"* ]]; then
    # Extract owner/repo from URL
    if [[ $REPO_URL == *".git" ]]; then
        REPO_PATH=$(echo $REPO_URL | sed 's/.*github\.com[:/]\(.*\)\.git/\1/')
    else
        REPO_PATH=$(echo $REPO_URL | sed 's/.*github\.com[:/]\(.*\)/\1/')
    fi
    echo "📁 Repository: $REPO_PATH"
    echo "🔗 GitHub URL: https://github.com/$REPO_PATH"
else
    echo "❌ Error: Not a GitHub repository"
    exit 1
fi

echo ""
echo "✅ Required GitHub Secrets for AWS Deployment:"
echo "=============================================="

# List of required secrets
REQUIRED_SECRETS=(
    "AWS_ACCESS_KEY_ID"
    "AWS_SECRET_ACCESS_KEY" 
    "AWS_REGION"
    "JWT_SECRET"
    "OPENAI_API_KEY"
)

echo ""
for secret in "${REQUIRED_SECRETS[@]}"; do
    case $secret in
        "AWS_ACCESS_KEY_ID")
            echo "🔑 $secret"
            echo "   Description: AWS Access Key ID for programmatic access"
            echo "   Format: AKIA... (20 characters starting with AKIA)"
            echo "   Source: AWS IAM Console > Users > Security credentials"
            ;;
        "AWS_SECRET_ACCESS_KEY")
            echo "🔐 $secret"
            echo "   Description: AWS Secret Access Key (keep this secure!)"
            echo "   Format: 40-character alphanumeric string"
            echo "   Source: AWS IAM Console > Users > Security credentials"
            ;;
        "AWS_REGION")
            echo "🌍 $secret"
            echo "   Description: AWS region for deployment"
            echo "   Format: us-east-1, us-west-2, eu-west-1, etc."
            echo "   Recommended: us-east-1 (cheapest for new accounts)"
            ;;
        "JWT_SECRET")
            echo "🔒 $secret"
            echo "   Description: Secret key for JWT token signing"
            echo "   Format: Long random string (64+ characters recommended)"
            echo "   Generate with: openssl rand -base64 64"
            ;;
        "OPENAI_API_KEY")
            echo "🤖 $secret"
            echo "   Description: OpenAI API key for recommendations (optional)"
            echo "   Format: sk-... (starts with sk-)"
            echo "   Source: https://platform.openai.com/api-keys"
            echo "   Note: Can be empty for MVP deployment"
            ;;
    esac
    echo ""
done

echo "📋 How to Add/Verify Secrets:"
echo "============================"
echo "1. Go to: https://github.com/$REPO_PATH/settings/secrets/actions"
echo "2. Click 'New repository secret' for each missing secret"
echo "3. Enter the exact name (case-sensitive) and value"
echo "4. Click 'Add secret'"
echo ""

echo "🚀 Test Deployment:"
echo "=================="
echo "After adding all secrets, you can:"
echo "1. Push changes to trigger automatic deployment:"
echo "   git push origin main"
echo ""
echo "2. Or manually trigger deployment:"
echo "   ./trigger-deployment.sh"
echo ""
echo "3. Monitor deployment progress:"
echo "   https://github.com/$REPO_PATH/actions"
echo ""

echo "🔍 Verify Secrets Are Set:"
echo "========================="
echo "Visit: https://github.com/$REPO_PATH/settings/secrets/actions"
echo "You should see all 5 secrets listed (values are hidden for security)"
echo ""

echo "⚠️  Important Notes:"
echo "==================="
echo "• Secret values are hidden once saved (this is normal)"
echo "• AWS credentials should have PowerUserAccess or equivalent permissions"
echo "• JWT_SECRET should be the same value used in backend/src/main/resources/application-dev.yaml"
echo "• OPENAI_API_KEY can be empty for MVP (recommendations will use fallback)"
echo "• Never commit secrets to git - always use GitHub Secrets"
echo ""

echo "✅ Next Steps:"
echo "============="
echo "1. Verify all 5 secrets are configured in GitHub"
echo "2. Test AWS credentials locally (optional): ./test-aws-creds.sh"
echo "3. Trigger deployment: ./trigger-deployment.sh"
echo "4. Monitor deployment: https://github.com/$REPO_PATH/actions"
echo ""

echo "🆘 Need Help?"
echo "============="
echo "• AWS Setup Guide: ./AWS-SETUP.md"
echo "• GitHub Actions Guide: ./GITHUB-DEPLOYMENT.md"
echo "• Test AWS Credentials: ./test-aws-creds.sh"
echo ""
