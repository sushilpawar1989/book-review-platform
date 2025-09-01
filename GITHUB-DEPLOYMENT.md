# 🚀 GitHub Actions Deployment Guide

Automatic deployment to AWS using GitHub Actions - deploy on every push to main!

## 🎯 Quick Setup (5 Minutes)

### 1. 🔑 Add AWS Credentials to GitHub

Go to: https://github.com/sushilpawar1989/book-review-platform/settings/secrets/actions

**Add these secrets:**
```bash
AWS_ACCESS_KEY_ID = AKIA... (from AWS IAM)
AWS_SECRET_ACCESS_KEY = ... (from AWS IAM)
AWS_REGION = us-east-1
JWT_SECRET = your-super-secret-jwt-key-make-it-long-and-random
OPENAI_API_KEY = sk-... (optional)
```

### 2. 🚀 Trigger Deployment

**Option A: Automatic (Recommended)**
```bash
# Any push to main triggers deployment
git add .
git commit -m "deploy: initial AWS deployment"
git push origin main
```

**Option B: Use Helper Script**
```bash
# Interactive deployment trigger
./trigger-deployment.sh
```

**Option C: Manual Trigger**
1. Go to: https://github.com/sushilpawar1989/book-review-platform/actions
2. Click "🚀 Deploy to AWS"
3. Click "Run workflow"
4. Select environment and click "Run workflow"

### 3. 📊 Monitor Deployment

**GitHub Actions Dashboard:**
https://github.com/sushilpawar1989/book-review-platform/actions

**Deployment Steps:**
1. 🏗️ **Build Images** (3-5 min) - Docker images for backend/frontend
2. 🏗️ **Deploy Infrastructure** (5-8 min) - Terraform creates AWS resources
3. 🚀 **Deploy Application** (2-3 min) - Updates ECS service
4. 🔍 **Verify Health** (1 min) - Tests application endpoints

**Total Time: ~10-15 minutes**

## 📋 What Gets Deployed

### AWS Resources Created
- **ECS Fargate Cluster** - Container orchestration
- **Application Load Balancer** - Traffic distribution
- **ECR Repositories** - Docker image storage
- **VPC & Networking** - Secure network setup
- **CloudWatch Logs** - Application monitoring
- **Security Groups** - Network security

### Application Components
- **Backend API** - Spring Boot application
- **Database** - H2 in-memory (production-ready)
- **Load Balancer** - Public endpoint
- **Auto Scaling** - Handles traffic spikes
- **Health Checks** - Automatic recovery

## 🔍 Monitoring Your Deployment

### GitHub Actions
```bash
# View workflow runs
https://github.com/sushilpawar1989/book-review-platform/actions

# Check specific deployment
Click on any workflow run → View logs
```

### AWS Console
```bash
# ECS Service Status
https://console.aws.amazon.com/ecs/home?region=us-east-1#/clusters

# Load Balancer
https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#LoadBalancers

# CloudWatch Logs
https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#logsV2:log-groups
```

### Application Health
```bash
# After deployment completes, test these endpoints:
curl https://your-alb-url.us-east-1.elb.amazonaws.com/actuator/health
curl https://your-alb-url.us-east-1.elb.amazonaws.com/api/v1/books
```

## 🛠️ Customizing Deployment

### Environment Variables
Update in GitHub Secrets:
```bash
JWT_SECRET = new-secret-key
OPENAI_API_KEY = sk-new-key
SPRING_PROFILES_ACTIVE = production
```

### Infrastructure Changes
```bash
# Edit Terraform variables
vim infra/backend/variables.tf

# Common changes:
# - Instance size (cpu/memory)
# - Scaling settings (desired_count)
# - Region (aws_region)
```

### Application Configuration
```bash
# Backend config
vim backend/src/main/resources/application.yaml

# Frontend config  
vim frontend/src/api/client.ts
```

## 🚨 Troubleshooting

### Deployment Fails

**1. Check GitHub Actions Logs**
```bash
# Go to Actions tab → Click failed workflow → View logs
# Look for red ❌ steps and error messages
```

**2. Common Issues & Solutions**

| Issue | Solution |
|-------|----------|
| AWS credentials invalid | Update GitHub secrets with correct AWS keys |
| Terraform fails | Check AWS permissions, resource limits |
| Docker build fails | Fix Dockerfile syntax, check dependencies |
| ECS deployment timeout | Check application logs, health check path |
| Health check fails | Verify application starts correctly |

**3. AWS Resource Issues**
```bash
# Check ECS service
aws ecs describe-services --cluster book-review-platform-cluster --services book-review-platform-service

# Check task logs
aws logs tail /ecs/book-review-platform --follow

# Check load balancer targets
aws elbv2 describe-target-health --target-group-arn arn:aws:elasticloadbalancing:...
```

### Rollback Deployment
```bash
# Option 1: Revert git commit
git revert HEAD
git push origin main

# Option 2: Deploy previous image
# Go to GitHub Actions → Re-run previous successful workflow

# Option 3: Manual rollback via AWS Console
# ECS → Service → Update Service → Previous task definition
```

## 💰 Cost Management

### Expected Monthly Costs
```bash
ECS Fargate (2 tasks): ~$15/month
Load Balancer: ~$16/month  
ECR Storage: ~$1/month
CloudWatch Logs: ~$1/month
NAT Gateway: ~$32/month
Total: ~$65/month
```

### Cost Optimization
```bash
# Reduce for development
# Edit infra/backend/variables.tf:
desired_count = 1  # Instead of 2
cpu = 256         # Instead of 512
memory = 512      # Instead of 1024

# Destroy when not needed
terraform destroy
```

## 🔒 Security Best Practices

### GitHub Secrets
- ✅ Use repository secrets for sensitive data
- ✅ Rotate AWS keys regularly
- ✅ Use least privilege IAM policies
- ✅ Enable 2FA on GitHub account

### AWS Security
- ✅ Private subnets for ECS tasks
- ✅ Security groups with minimal ports
- ✅ HTTPS-only load balancer
- ✅ VPC endpoints for AWS services

## 📈 Advanced Features

### Blue/Green Deployment
```yaml
# Add to deploy.yml
strategy:
  type: blue-green
  maximum_percent: 200
  minimum_healthy_percent: 100
```

### Multi-Environment
```bash
# Create separate workflows for:
# - deploy-dev.yml (develop branch)
# - deploy-staging.yml (staging branch)  
# - deploy-prod.yml (main branch)
```

### Slack Notifications
```bash
# Add to GitHub Secrets:
SLACK_WEBHOOK_URL = https://hooks.slack.com/services/...

# Notifications will be sent automatically
```

## 🎯 Next Steps

### After First Deployment
1. **Test Application** - Verify all features work
2. **Set Up Monitoring** - CloudWatch dashboards
3. **Configure Domain** - Route 53 + SSL certificate
4. **Database Migration** - Move to RDS PostgreSQL
5. **CDN Setup** - CloudFront for frontend

### Production Readiness
1. **Backup Strategy** - Database backups
2. **Disaster Recovery** - Multi-AZ deployment
3. **Performance Tuning** - Auto scaling policies
4. **Security Hardening** - WAF, security scanning
5. **Compliance** - Logging, audit trails

---

## 🚀 Ready to Deploy?

```bash
# Quick deployment:
./trigger-deployment.sh

# Or manual:
git add .
git commit -m "deploy: to AWS"
git push origin main
```

**Monitor at:** https://github.com/sushilpawar1989/book-review-platform/actions

🎉 **Your app will be live on AWS in ~15 minutes!**
