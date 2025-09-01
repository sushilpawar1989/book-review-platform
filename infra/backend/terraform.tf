# Terraform Configuration
terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  # Optional: Use S3 backend for state management
  # backend "s3" {
  #   bucket = "your-terraform-state-bucket"
  #   key    = "book-review-platform/terraform.tfstate"
  #   region = "us-east-1"
  # }
}

# AWS Provider
provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "book-review-platform"
      Environment = var.environment
      ManagedBy   = "terraform"
      Owner       = "sushilpawar1989"
    }
  }
}
