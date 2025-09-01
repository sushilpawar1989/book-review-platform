terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

# Data sources for existing resources
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

data "aws_ecr_repository" "backend" {
  name = "book-review-backend"
}

data "aws_ecr_repository" "frontend" {
  name = "book-review-frontend"
}

# Security Group for ALB
resource "aws_security_group" "alb" {
  name_prefix = "book-review-alb-"
  description = "Security group for Application Load Balancer"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name    = "book-review-alb-sg"
    Project = "book-review-platform"
  }
}

# Security Group for ECS Tasks
resource "aws_security_group" "ecs_tasks" {
  name_prefix = "book-review-ecs-"
  description = "Security group for ECS tasks"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name    = "book-review-ecs-sg"
    Project = "book-review-platform"
  }
}

# Application Load Balancer
resource "aws_lb" "main" {
  name               = "book-review-minimal"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = data.aws_subnets.default.ids

  enable_deletion_protection = false

  tags = {
    Name    = "book-review-minimal"
    Project = "book-review-platform"
  }
}

# Target Group for Backend
resource "aws_lb_target_group" "backend" {
  name     = "book-review-backend"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
    path                = "/api/books"
    matcher             = "200"
  }

  tags = {
    Name    = "book-review-backend"
    Project = "book-review-platform"
  }
}

# Target Group for Frontend
resource "aws_lb_target_group" "frontend" {
  name     = "book-review-frontend"
  port     = 80
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
    path                = "/"
    matcher             = "200"
  }

  tags = {
    Name    = "book-review-frontend"
    Project = "book-review-platform"
  }
}

# ALB Listener
resource "aws_lb_listener" "main" {
  load_balancer_arn = aws_lb.main.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }
}

# ALB Listener Rule for API
resource "aws_lb_listener_rule" "api" {
  listener_arn = aws_lb_listener.main.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }
}

# IAM Role for ECS Task Execution
resource "aws_iam_role" "ecs_execution_role" {
  name = "book-review-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name    = "book-review-ecs-execution-role"
    Project = "book-review-platform"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# IAM Role for ECS Task
resource "aws_iam_role" "ecs_task_role" {
  name = "book-review-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name    = "book-review-ecs-task-role"
    Project = "book-review-platform"
  }
}

# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = "book-review-minimal"

  setting {
    name  = "containerInsights"
    value = "disabled"  # Disable to save costs
  }

  tags = {
    Name    = "book-review-minimal"
    Project = "book-review-platform"
  }
}

# ECS Task Definition for Backend
resource "aws_ecs_task_definition" "backend" {
  family                   = "book-review-backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"  # Minimal CPU
  memory                   = "512"  # Minimal memory
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn           = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name  = "book-review-backend"
      image = "${data.aws_ecr_repository.backend.repository_url}:latest"
      
      portMappings = [
        {
          containerPort = 8080
          protocol      = "tcp"
        }
      ]
      
      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "prod"
        },
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:h2:mem:testdb"
        },
        {
          name  = "SPRING_DATASOURCE_DRIVER_CLASS_NAME"
          value = "org.h2.Driver"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = "sa"
        },
        {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = ""
        },
        {
          name  = "SPRING_H2_CONSOLE_ENABLED"
          value = "true"
        },
        {
          name  = "SPRING_JPA_DATABASE_PLATFORM"
          value = "org.hibernate.dialect.H2Dialect"
        },
        {
          name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
          value = "create-drop"
        },
        {
          name  = "SPRING_CACHE_TYPE"
          value = "none"
        },
        {
          name  = "SPRING_CACHE_CACHE_NAMES"
          value = ""
        },
        {
          name  = "SPRING_SQL_INIT_MODE"
          value = "always"
        },
        {
          name  = "SPRING_SQL_INIT_ORDER"
          value = "1"
        },
        {
          name  = "SPRING_SQL_INIT_MODE"
          value = "never"
        }
      ]
      
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/book-review-backend"
          awslogs-region        = "us-east-1"
          awslogs-stream-prefix = "ecs"
        }
      }
      
      essential = true
    }
  ])

  tags = {
    Name    = "book-review-backend"
    Project = "book-review-platform"
  }
}

# ECS Task Definition for Frontend
resource "aws_ecs_task_definition" "frontend" {
  family                   = "book-review-frontend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"  # Minimal CPU
  memory                   = "512"  # Minimal memory
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn           = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name  = "book-review-frontend"
      image = "${data.aws_ecr_repository.frontend.repository_url}:latest"
      
      portMappings = [
        {
          containerPort = 80
          protocol      = "tcp"
        }
      ]
      
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/book-review-frontend"
          awslogs-region        = "us-east-1"
          awslogs-stream-prefix = "ecs"
        }
      }
      
      essential = true
    }
  ])

  tags = {
    Name    = "book-review-frontend"
    Project = "book-review-platform"
  }
}

# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/book-review-backend"
  retention_in_days = 1  # Minimal retention to save costs

  tags = {
    Name    = "book-review-backend-logs"
    Project = "book-review-platform"
  }
}

resource "aws_cloudwatch_log_group" "frontend" {
  name              = "/ecs/book-review-frontend"
  retention_in_days = 1  # Minimal retention to save costs

  tags = {
    Name    = "book-review-frontend-logs"
    Project = "book-review-platform"
  }
}

# ECS Service for Backend
resource "aws_ecs_service" "backend" {
  name            = "book-review-backend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = 1  # Minimal count

  launch_type = "FARGATE"

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true  # Required for Fargate in public subnets
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend.arn
    container_name   = "book-review-backend"
    container_port   = 8080
  }

  depends_on = [aws_lb_listener.main]

  tags = {
    Name    = "book-review-backend-service"
    Project = "book-review-platform"
  }
}

# ECS Service for Frontend
resource "aws_ecs_service" "frontend" {
  name            = "book-review-frontend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = 1  # Minimal count

  launch_type = "FARGATE"

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true  # Required for Fargate in public subnets
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.frontend.arn
    container_name   = "book-review-frontend"
    container_port   = 80
  }

  depends_on = [aws_lb_listener.main]

  tags = {
    Name    = "book-review-frontend-service"
    Project = "book-review-platform"
  }
}

# Outputs
output "alb_dns_name" {
  description = "DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "frontend_url" {
  description = "URL for the frontend application"
  value       = "http://${aws_lb.main.dns_name}"
}

output "backend_url" {
  description = "URL for the backend API"
  value       = "http://${aws_lb.main.dns_name}/api"
}
