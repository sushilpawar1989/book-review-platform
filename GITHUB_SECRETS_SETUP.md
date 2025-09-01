# GitHub Repository Secrets Setup

This document explains how to set up the required secrets in your GitHub repository for the CI/CD pipeline to work properly.

## Required Secrets

You need to add the following secrets to your GitHub repository:

### 1. JWT Configuration
- **JWT_SECRET**: A secure random string (at least 32 characters) for JWT token signing
- **JWT_EXPIRATION**: JWT token expiration time in milliseconds (default: 86400000 = 24 hours)
- **JWT_REFRESH_EXPIRATION**: JWT refresh token expiration time in milliseconds (default: 604800000 = 7 days)

### 2. OpenAI API Configuration
- **OPENAI_API_KEY**: Your OpenAI API key for AI-powered book recommendations

### 3. AWS Configuration
- **AWS_ACCESS_KEY_ID**: AWS access key for ECR and infrastructure deployment
- **AWS_SECRET_ACCESS_KEY**: AWS secret key for ECR and infrastructure deployment
- **AWS_REGION**: AWS region for deployment (default: us-east-1)

## How to Add Secrets

1. Go to your GitHub repository
2. Click on **Settings** tab
3. In the left sidebar, click **Secrets and variables** → **Actions**
4. Click **New repository secret**
5. Add each secret with the exact names listed above

## Example Values

### JWT_SECRET
Generate a secure random string:
```bash
# Option 1: Using openssl
openssl rand -base64 32

# Option 2: Using Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"

# Option 3: Using Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

### JWT_EXPIRATION
- 86400000 = 24 hours
- 3600000 = 1 hour
- 1800000 = 30 minutes

### JWT_REFRESH_EXPIRATION
- 604800000 = 7 days
- 2592000000 = 30 days

## Security Notes

- **Never commit secrets to your repository**
- **Use strong, unique values for JWT_SECRET**
- **Rotate your AWS credentials regularly**
- **Keep your OpenAI API key secure**

## Testing the Setup

After adding the secrets, push a commit to the `main` branch to trigger the CI/CD pipeline. The workflow will:

1. Build the backend and frontend
2. Use the secrets to build Docker images with proper environment variables
3. Push images to AWS ECR
4. Deploy to AWS infrastructure

## Troubleshooting

If the pipeline fails:

1. Check that all required secrets are set
2. Verify secret names match exactly (case-sensitive)
3. Ensure AWS credentials have proper permissions
4. Check the GitHub Actions logs for specific error messages
