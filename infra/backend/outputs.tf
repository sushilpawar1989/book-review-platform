# Outputs for Book Review Platform (Assignment)
# Using H2 in-memory database - no external DB needed

output "backend_url" {
  description = "Backend application URL"
  value       = "http://${aws_lb.main.dns_name}"
}

output "frontend_url" {
  description = "Frontend application URL"
  value       = "http://${aws_lb.main.dns_name}:3000"
}

output "ecr_backend_repository" {
  description = "ECR repository URL for backend"
  value       = aws_ecr_repository.backend.repository_url
}

output "ecr_frontend_repository" {
  description = "ECR repository URL for frontend"
  value       = aws_ecr_repository.frontend.repository_url
}

output "vpc_id" {
  description = "VPC ID where resources are deployed"
  value       = aws_vpc.main.id
}

output "alb_dns_name" {
  description = "Application Load Balancer DNS name"
  value       = aws_lb.main.dns_name
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.main.name
}

output "backend_service_name" {
  description = "Backend ECS service name"
  value       = aws_ecs_service.backend.name
}

output "frontend_service_name" {
  description = "Frontend ECS service name"
  value       = aws_ecs_service.frontend.name
}
