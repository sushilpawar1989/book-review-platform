# Terraform variables for Book Review Platform
# These values match the docker-compose.yml environment configuration

# AWS Configuration
aws_region = "us-east-1"
environment = "dev"
project_name = "book-review-platform"

# VPC Configuration
vpc_cidr = "10.0.0.0/16"
public_subnet_cidrs = ["10.0.1.0/24", "10.0.2.0/24"]
private_subnet_cidrs = ["10.0.3.0/24", "10.0.4.0/24"]

# Backend Configuration
container_port = 8080
cpu = 512
memory = 1024
desired_count = 2
health_check_path = "/actuator/health"

# Frontend Configuration
frontend_cpu = 256
frontend_memory = 512
frontend_desired_count = 2
frontend_container_port = 80
frontend_health_check_path = "/"

# JWT Configuration (matching docker-compose)
jwt_secret = "your-super-secure-jwt-secret-key-that-is-long-enough-for-256-bits"
jwt_expiration = "86400000"
jwt_refresh_expiration = "604800000"

# Database Configuration (optimized for Flyway migrations)
database_url = "jdbc:h2:mem:bookreview"
hibernate_ddl_auto = "none"
sql_init_mode = "never"

# Cache Configuration (matching docker-compose)
cache_type = "none"
cache_names = ""

# OpenAI Configuration (set this in production)
openai_api_key = ""

# Logging
enable_logging = true

# Domain (optional)
domain_name = ""
