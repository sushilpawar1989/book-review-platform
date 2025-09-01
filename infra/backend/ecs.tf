# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-ecs-cluster"
    Environment = var.environment
  }
}

# ECS Task Definition for Backend
resource "aws_ecs_task_definition" "backend" {
  family                   = "${var.project_name}-backend-${var.environment}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.cpu
  memory                   = var.memory
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name  = "${var.project_name}-backend"
      image = "${aws_ecr_repository.backend.repository_url}:latest"
      
      # Command to override Spring Boot configuration with command-line arguments
      command = [
        "java",
        "-jar",
        "/app/app.jar",
        "--spring.profiles.active=${var.environment}",
        "--spring.datasource.url=${var.database_url}",
        "--spring.jpa.hibernate.ddl-auto=none",
        "--spring.sql.init.mode=never",
        "--spring.jpa.defer-datasource-initialization=false",
        "--spring.flyway.enabled=true",
        "--spring.flyway.baseline-on-migrate=true",
        "--spring.flyway.baseline-version=0",
        "--spring.flyway.locations=classpath:db/migration"
      ]
      
      portMappings = [
        {
          containerPort = var.container_port
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "JWT_SECRET"
          value = var.jwt_secret
        },
        {
          name  = "SPRING_SECURITY_JWT_EXPIRATION"
          value = var.jwt_expiration
        },
        {
          name  = "SPRING_SECURITY_JWT_REFRESH_EXPIRATION"
          value = var.jwt_refresh_expiration
        },
        {
          name  = "SPRING_CACHE_TYPE"
          value = var.cache_type
        }
      ]

      secrets = var.openai_api_key != "" ? [
        {
          name      = "OPENAI_API_KEY"
          valueFrom = var.openai_api_key
        }
      ] : []

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.backend.name
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }

      essential = true
    }
  ])

  tags = {
    Name        = "${var.project_name}-backend-task-def-${var.environment}"
    Environment = var.environment
  }
}

# ECS Service for Backend
resource "aws_ecs_service" "backend" {
  name            = "${var.project_name}-backend-${var.environment}"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = aws_subnet.public[*].id
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend.arn
    container_name   = "${var.project_name}-backend"
    container_port   = var.container_port
  }

  depends_on = [aws_lb_listener.backend]

  tags = {
    Name        = "${var.project_name}-backend-service-${var.environment}"
    Environment = var.environment
  }
}

# ECS Task Definition for Frontend
resource "aws_ecs_task_definition" "frontend" {
  family                   = "${var.project_name}-frontend-${var.environment}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.frontend_cpu
  memory                   = var.frontend_memory
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name  = "${var.project_name}-frontend"
      image = "${aws_ecr_repository.frontend.repository_url}:latest"
      
      portMappings = [
        {
          containerPort = var.frontend_container_port
          protocol      = "tcp"
        }
      ]

      environment = []

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.frontend.name
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }

      essential = true
    }
  ])

  tags = {
    Name        = "${var.project_name}-frontend-task-def-${var.environment}"
    Environment = var.environment
  }
}

# ECS Service for Frontend
resource "aws_ecs_service" "frontend" {
  name            = "${var.project_name}-frontend-${var.environment}"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = var.frontend_desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = aws_subnet.public[*].id
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.frontend.arn
    container_name   = "${var.project_name}-frontend"
    container_port   = var.frontend_container_port
  }

  depends_on = [aws_lb_listener.frontend]

  tags = {
    Name        = "${var.project_name}-frontend-service-${var.environment}"
    Environment = var.environment
  }
}

# CloudWatch Log Group for Backend
resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/${var.project_name}-backend-${var.environment}"
  retention_in_days = 7

  tags = {
    Name        = "${var.project_name}-backend-logs-${var.environment}"
    Environment = var.environment
  }
}

# CloudWatch Log Group for Frontend
resource "aws_cloudwatch_log_group" "frontend" {
  name              = "/ecs/${var.project_name}-frontend-${var.environment}"
  retention_in_days = 7

  tags = {
    Name        = "${var.project_name}-frontend-logs-${var.environment}"
    Environment = var.environment
  }
}

