# ☁️ AWS Deployment Guide

Complete guide to deploy your Book Review Platform on AWS using ECS Fargate.

## 🎯 Prerequisites

### 1. AWS Account Setup
- ✅ AWS Account created
- ✅ Credit card added (for billing)
- ✅ Account verified

### 2. Required Tools
```bash
# Install AWS CLI
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
sudo installer -pkg AWSCLIV2.pkg -target /

# Install Terraform
brew install terraform

# Verify installations
aws --version
terraform --version
```

## 🔑 Step 1: AWS Credentials Setup

### Create IAM User
1. **Login to AWS Console**: https://console.aws.amazon.com
2. **Navigate to IAM**: Search "IAM" → Users → Add users
3. **User Details**:
   - Username: `github-actions-deploy`
   - Access type: ✅ Programmatic access
4. **Permissions**: Attach existing policies:
   ```
   AmazonECS_FullAccess
   AmazonEC2FullAccess
   AmazonVPCFullAccess
   IAMFullAccess
   CloudWatchFullAccess
   ElasticLoadBalancingFullAccess
   ```
5. **Save Credentials**:
   - Access Key ID: `AKIA...`
   - Secret Access Key: `...` (save immediately!)

### Configure Local AWS CLI
```bash
aws configure
# AWS Access Key ID: [paste your key]
# AWS Secret Access Key: [paste your secret]
# Default region: us-east-1
# Default output format: json
```

### Test Configuration
```bash
aws sts get-caller-identity
# Should return your account info
```

## 🏗️ Step 2: Deploy Infrastructure

### Option A: Automated Deployment
```bash
# Run the deployment script
./deploy-aws.sh

# Follow the prompts:
# - JWT Secret (or use default)
# - OpenAI API Key (optional)
# - AWS Region (default: us-east-1)
# - Environment (dev/staging/prod)
```

### Option B: Manual Deployment
```bash
cd infra/backend

# Initialize Terraform
terraform init

# Create deployment plan
terraform plan \
  -var="jwt_secret=your-super-secret-jwt-key" \
  -var="openai_api_key=your-openai-key" \
  -var="environment=production"

# Apply the plan
terraform apply
```

## 🚀 Step 3: GitHub Actions Integration

### Add Repository Secrets
Go to: https://github.com/sushilpawar1989/book-review-platform/settings/secrets/actions

Add these secrets:
```
AWS_ACCESS_KEY_ID = AKIA...
AWS_SECRET_ACCESS_KEY = ...
AWS_REGION = us-east-1
JWT_SECRET = your-super-secret-jwt-key
OPENAI_API_KEY = sk-... (optional)
```

### Trigger Deployment
```bash
# Push to main branch triggers deployment
git add .
git commit -m "feat: configure AWS deployment"
git push origin main

# Or manually trigger via GitHub Actions
```

## 📊 Step 4: Verify Deployment

### Check AWS Resources
```bash
# List ECS services
aws ecs list-services --cluster book-review-platform-dev

# Check service status
aws ecs describe-services \
  --cluster book-review-platform-dev \
  --services book-review-platform-dev-service

# Get load balancer URL
aws elbv2 describe-load-balancers \
  --names book-review-platform-dev-alb
```

### Test Application
```bash
# Get the load balancer URL from Terraform output
terraform output alb_dns_name

# Test the backend API
curl http://your-alb-url.us-east-1.elb.amazonaws.com/actuator/health
```

## 🔧 Step 5: Configuration

### Environment Variables
The application uses these environment variables:
```bash
SPRING_PROFILES_ACTIVE=production
JWT_SECRET=your-jwt-secret
OPENAI_API_KEY=your-openai-key
SPRING_DATASOURCE_URL=jdbc:h2:mem:bookreview
```

### Custom Domain (Optional)
1. **Register Domain**: Route 53 or external provider
2. **SSL Certificate**: AWS Certificate Manager
3. **Update Terraform**:
   ```hcl
   variable "domain_name" {
     default = "api.yourdomain.com"
   }
   ```

## 📈 Step 6: Monitoring & Logs

### CloudWatch Logs
```bash
# View application logs
aws logs describe-log-groups --log-group-name-prefix /ecs/book-review

# Stream logs
aws logs tail /ecs/book-review-platform-dev --follow
```

### Monitoring Dashboard
- **ECS Console**: https://console.aws.amazon.com/ecs
- **CloudWatch**: https://console.aws.amazon.com/cloudwatch
- **Load Balancer**: https://console.aws.amazon.com/ec2/v2/home#LoadBalancers

## 💰 Cost Management

### Expected Costs (us-east-1)
```
ECS Fargate (2 tasks, 0.5 vCPU, 1GB RAM): ~$15/month
Application Load Balancer: ~$16/month
CloudWatch Logs: ~$1/month
NAT Gateway: ~$32/month (if using private subnets)

Total: ~$64/month for production setup
```

### Cost Optimization
```bash
# Use smaller instance sizes for dev
terraform apply -var="cpu=256" -var="memory=512"

# Reduce task count for dev
terraform apply -var="desired_count=1"

# Destroy when not needed
terraform destroy
```

## 🔒 Security Best Practices

### IAM Policies
- Use least privilege access
- Rotate access keys regularly
- Enable MFA for console access

### Network Security
- Private subnets for ECS tasks
- Security groups with minimal ports
- VPC endpoints for AWS services

### Application Security
- HTTPS only (ALB SSL termination)
- Secure JWT secrets
- Environment variable encryption

## 🚨 Troubleshooting

### Common Issues

#### ECS Task Fails to Start
```bash
# Check task definition
aws ecs describe-task-definition --task-definition book-review-platform-dev

# Check service events
aws ecs describe-services --cluster book-review-platform-dev --services book-review-platform-dev-service

# Check task logs
aws logs tail /ecs/book-review-platform-dev --follow
```

#### Load Balancer Health Check Fails
```bash
# Check target group health
aws elbv2 describe-target-health --target-group-arn arn:aws:elasticloadbalancing:...

# Verify health check path
curl http://task-ip:8080/actuator/health
```

#### High Costs
```bash
# Check resource usage
aws ce get-cost-and-usage --time-period Start=2024-01-01,End=2024-01-31 --granularity MONTHLY --metrics BlendedCost

# Identify expensive resources
aws ce get-dimension-values --dimension SERVICE --time-period Start=2024-01-01,End=2024-01-31
```

## 📚 Additional Resources

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS CLI Reference](https://docs.aws.amazon.com/cli/latest/reference/)
- [Spring Boot on AWS](https://spring.io/guides/gs/spring-boot-docker/)

## 🆘 Support

### AWS Support
- **Basic**: Included with account
- **Developer**: $29/month
- **Business**: $100/month

### Community Resources
- [AWS Forums](https://forums.aws.amazon.com/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/amazon-web-services)
- [GitHub Issues](https://github.com/sushilpawar1989/book-review-platform/issues)

---

🎉 **Your Book Review Platform is now ready for AWS deployment!**

Run `./deploy-aws.sh` to get started!
