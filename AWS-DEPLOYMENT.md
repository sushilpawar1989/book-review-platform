# AWS Deployment Guide for Book Review Platform

This guide explains how to deploy your Book Review Platform to AWS using Terraform, with the same environment variables and configuration as your local Docker setup.

## 🏗️ Architecture Overview

The deployment creates the following AWS resources:

- **VPC** with public and private subnets
- **ECS Fargate Cluster** for running containers
- **ECR Repositories** for storing Docker images
- **Application Load Balancer** for routing traffic
- **Security Groups** for network security
- **CloudWatch Logs** for application logging

## 📋 Prerequisites

1. **AWS CLI** installed and configured
2. **Terraform** installed (version >= 1.0)
3. **Docker** running locally
4. **AWS credentials** with appropriate permissions

### Required AWS Permissions

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ec2:*",
                "ecs:*",
                "ecr:*",
                "elasticloadbalancing:*",
                "iam:*",
                "logs:*",
                "cloudwatch:*"
            ],
            "Resource": "*"
        }
    ]
}
```

## 🚀 Quick Deployment

### Option 1: Automated Deployment Script

```bash
# Make the script executable
chmod +x deploy-aws.sh

# Run the deployment
./deploy-aws.sh
```

This script will:
1. Initialize Terraform
2. Create infrastructure (if not exists)
3. Build and push Docker images to ECR
4. Deploy applications to ECS
5. Perform health checks

### Option 2: Manual Deployment

#### Step 1: Initialize Terraform

```bash
cd infra/backend
terraform init
```

#### Step 2: Review the Plan

```bash
terraform plan
```

#### Step 3: Apply the Configuration

```bash
terraform apply
```

#### Step 4: Build and Push Images

```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $(terraform output -raw ecr_backend_repository)

# Build and push backend
cd ../../backend
docker build -t book-review-backend .
docker tag book-review-backend:latest $(cd ../infra/backend && terraform output -raw ecr_backend_repository):latest
docker push $(cd ../infra/backend && terraform output -raw ecr_backend_repository):latest

# Build and push frontend
cd ../frontend
docker build -t book-review-frontend .
docker tag book-review-frontend:latest $(cd ../infra/backend && terraform output -raw ecr_frontend_repository):latest
docker push $(cd ../infra/backend && terraform output -raw ecr_frontend_repository):latest
```

## 🔧 Configuration

### Environment Variables

The deployment uses the same environment variables as your local Docker setup:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` |
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:h2:mem:bookreview` |
| `SPRING_SECURITY_JWT_SECRET` | JWT secret | Configurable |
| `SPRING_SECURITY_JWT_EXPIRATION` | JWT expiration | `86400000` |
| `SPRING_SECURITY_JWT_REFRESH_EXPIRATION` | JWT refresh expiration | `604800000` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode | `validate` |
| `SPRING_SQL_INIT_MODE` | SQL init mode | `never` |
| `SPRING_CACHE_TYPE` | Cache type | `none` |
| `OPENAI_API_KEY` | OpenAI API key | Configurable |
| `VITE_API_BASE_URL` | Frontend API base URL | Auto-configured |

### Customizing Configuration

Edit `infra/backend/terraform.tfvars` to customize:

```hcl
# JWT Configuration
jwt_secret = "your-custom-jwt-secret"
jwt_expiration = "86400000"
jwt_refresh_expiration = "604800000"

# OpenAI Configuration
openai_api_key = "your-openai-api-key"

# Resource Sizing
cpu = 1024
memory = 2048
frontend_cpu = 512
frontend_memory = 1024
```

## 🌐 Accessing Your Application

After deployment, you'll get:

- **Frontend**: `http://<alb-dns-name>:3000`
- **Backend**: `http://<alb-dns-name>`
- **Backend Health**: `http://<alb-dns-name>/actuator/health`

## 📊 Monitoring and Logs

### CloudWatch Logs

- Backend logs: `/ecs/book-review-platform-backend-dev`
- Frontend logs: `/ecs/book-review-platform-frontend-dev`

### Health Checks

- Backend: `/actuator/health` (every 30s)
- Frontend: `/` (every 30s)

## 🔄 Updating the Deployment

### Update Application Code

```bash
# Build new images
docker build -t book-review-backend:latest ../../backend
docker build -t book-review-frontend:latest ../../frontend

# Push to ECR
docker push <backend-ecr-url>:latest
docker push <frontend-ecr-url>:latest

# Force new deployment
cd infra/backend
terraform apply
```

### Update Infrastructure

```bash
cd infra/backend
terraform plan
terraform apply
```

## 🧹 Cleanup

To remove all resources:

```bash
cd infra/backend
terraform destroy
```

## 🚨 Troubleshooting

### Common Issues

1. **ECS Service Not Starting**
   - Check CloudWatch logs
   - Verify ECR image exists
   - Check security group rules

2. **Health Check Failures**
   - Verify application is responding on health check path
   - Check security group allows ALB traffic

3. **Image Pull Errors**
   - Verify ECR login
   - Check image tags match task definition

### Debug Commands

```bash
# Check ECS service status
aws ecs describe-services --cluster <cluster-name> --services <service-name> --region us-east-1

# View CloudWatch logs
aws logs tail /ecs/book-review-platform-backend-dev --follow --region us-east-1

# Check ALB target health
aws elbv2 describe-target-health --target-group-arn <target-group-arn> --region us-east-1
```

## 📚 Additional Resources

- [ECS Fargate Documentation](https://docs.aws.amazon.com/ecs/latest/userguide/what-is-fargate.html)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [ECR Best Practices](https://docs.aws.amazon.com/ecr/latest/userguide/best-practices.html)

## 🔐 Security Notes

- JWT secrets should be unique per environment
- Consider using AWS Secrets Manager for sensitive values
- Enable VPC Flow Logs for network monitoring
- Use least-privilege IAM policies
- Enable CloudTrail for API auditing

---

**Note**: This deployment uses H2 in-memory database for demonstration. For production, consider using RDS or Aurora for persistent data storage.
