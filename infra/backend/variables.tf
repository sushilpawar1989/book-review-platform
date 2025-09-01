variable "aws_region" {
  description = "AWS region for resources"
  type        = string
  default     = "us-west-2"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "book-review-platform"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
}

variable "container_image" {
  description = "Docker image for the backend application"
  type        = string
  default     = ""
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
  default     = 8080
}

variable "cpu" {
  description = "CPU units for the task (1024 = 1 vCPU)"
  type        = number
  default     = 512
}

variable "memory" {
  description = "Memory for the task in MB"
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Desired number of tasks"
  type        = number
  default     = 2
}

variable "health_check_path" {
  description = "Health check path for the application"
  type        = string
  default     = "/actuator/health"
}

variable "jwt_secret" {
  description = "JWT secret for the application"
  type        = string
  sensitive   = true
  default     = "YjM2N2Q4ZWZhMTJjNGU5OGI3ZjNkMmE4YzVlNjRiOWQ5ZmE3YjNlOGM0ZDZhOWIyZjhlNzMxNGM5ZGI2YWY4ZGFiMzY3ZDhlZmExMmM0ZTk4Yjc="
}

variable "database_url" {
  description = "Database URL for the application"
  type        = string
  default     = "jdbc:h2:mem:testdb" # Default to H2 in-memory for demo
}

variable "enable_logging" {
  description = "Enable CloudWatch logging"
  type        = bool
  default     = true
}

variable "openai_api_key" {
  description = "OpenAI API key for recommendations"
  type        = string
  sensitive   = true
  default     = ""
}

variable "domain_name" {
  description = "Domain name for the application (optional)"
  type        = string
  default     = ""
}
