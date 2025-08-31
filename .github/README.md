# 🚀 CI/CD Pipeline Documentation

This directory contains GitHub Actions workflows for automated building, testing, and deployment of the Book Review Platform.

## 📋 Workflows Overview

### 1. 🔨 Build & Test (`build.yml`)
**Trigger**: Push to `main`, Pull Requests to `main`

**Backend Jobs:**
- ☕ Java 17 setup with Temurin distribution
- 📦 Gradle dependency caching
- 🧪 Unit and integration tests
- 🏗️ JAR artifact generation
- 📊 Test result uploads

**Frontend Jobs:**
- 📦 Node.js 18 setup with npm caching
- 🔍 ESLint code quality checks
- 🧪 Jest tests with coverage
- 🏗️ Production build generation
- 📊 Coverage report uploads

### 2. 🚀 CI/CD Pipeline (`ci-cd.yml`)
**Trigger**: Push to `main`/`develop`, Pull Requests to `main`

**Complete Pipeline:**
- 🏗️ **Build & Test**: Both backend and frontend
- 🔒 **Security Scan**: Trivy vulnerability scanning
- 🐳 **Docker Build**: Multi-platform container images
- 🚀 **Deploy**: Terraform-based AWS deployment
- 📢 **Notify**: Slack notifications

## 🔧 Setup Requirements

### Repository Secrets
Configure these secrets in your GitHub repository settings:

#### AWS Deployment
```bash
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key
AWS_REGION=us-east-1
```

#### Application Configuration
```bash
JWT_SECRET=your-super-secret-jwt-key-here
OPENAI_API_KEY=sk-your-openai-api-key
```

#### Docker Hub (Optional)
```bash
DOCKER_USERNAME=your-dockerhub-username
DOCKER_PASSWORD=your-dockerhub-password
```

#### Notifications (Optional)
```bash
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/...
```

### Environment Variables
```bash
VITE_API_BASE_URL=https://your-api-domain.com/api/v1
```

## 🏗️ Build Process

### Backend Build Steps
1. **Environment Setup**: Java 17, Gradle caching
2. **Code Quality**: Checkstyle validation
3. **Testing**: JUnit tests with JaCoCo coverage
4. **Build**: Executable JAR generation
5. **Artifacts**: Upload build outputs

### Frontend Build Steps
1. **Environment Setup**: Node.js 18, npm caching
2. **Dependencies**: npm ci for clean installs
3. **Code Quality**: ESLint and Prettier checks
4. **Type Safety**: TypeScript compilation
5. **Testing**: Jest with coverage reports
6. **Build**: Vite production build
7. **Artifacts**: Upload dist folder

## 🐳 Docker Support

### Multi-stage Builds
- **Backend**: OpenJDK 17 with optimized JVM settings
- **Frontend**: Node.js build + Nginx serving

### Container Features
- 🔒 Non-root user execution
- 💊 Health checks included
- 📊 Performance optimizations
- 🔄 Graceful shutdowns

### Local Development
```bash
# Start all services
docker-compose up -d

# Start with PostgreSQL
docker-compose --profile production up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 🚀 Deployment Pipeline

### Staging Environment
- **Trigger**: Push to `develop` branch
- **Target**: AWS ECS Fargate (staging)
- **Database**: H2 in-memory
- **Domain**: staging.your-domain.com

### Production Environment
- **Trigger**: Push to `main` branch
- **Target**: AWS ECS Fargate (production)
- **Database**: RDS PostgreSQL
- **Domain**: your-domain.com
- **CDN**: CloudFront distribution

### Infrastructure as Code
```bash
# Manual deployment
cd infra/backend
terraform init
terraform plan -var-file="production.tfvars"
terraform apply
```

## 📊 Monitoring & Observability

### Included Monitoring
- 🏥 **Health Checks**: Application and container health
- 📈 **Metrics**: JVM metrics via Micrometer
- 📋 **Logging**: Structured JSON logging
- 🔍 **Tracing**: Request correlation IDs

### External Integrations
- **Codecov**: Test coverage tracking
- **Slack**: Deployment notifications
- **AWS CloudWatch**: Application monitoring
- **GitHub Security**: Vulnerability alerts

## 🔒 Security Features

### Code Security
- 🔍 **Trivy Scanning**: Vulnerability detection
- 🔐 **Secret Detection**: GitHub secret scanning
- 📋 **SARIF Reports**: Security findings upload
- 🛡️ **Dependency Scanning**: Automated updates

### Runtime Security
- 👤 **Non-root Containers**: Principle of least privilege
- 🔒 **Security Headers**: OWASP recommendations
- 🌐 **HTTPS Only**: TLS encryption enforced
- 🔑 **JWT Security**: Secure token handling

## 🚨 Troubleshooting

### Common Issues

#### Build Failures
```bash
# Backend: Gradle permission issues
chmod +x backend/gradlew

# Frontend: Node modules cache
rm -rf frontend/node_modules frontend/package-lock.json
npm install
```

#### Docker Issues
```bash
# Clear Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

#### AWS Deployment Issues
```bash
# Check Terraform state
terraform show

# Validate configuration
terraform validate

# Debug with verbose logging
TF_LOG=DEBUG terraform apply
```

### Performance Optimization

#### Backend Optimization
- **JVM Tuning**: Container-aware settings
- **Connection Pooling**: HikariCP configuration
- **Caching**: Redis integration ready

#### Frontend Optimization
- **Bundle Splitting**: Vite code splitting
- **Asset Optimization**: Gzip compression
- **CDN Ready**: Static asset serving

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Spring Boot Production Ready](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all tests pass
5. Submit a pull request

The CI/CD pipeline will automatically validate your changes! 🎉
