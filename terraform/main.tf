terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.67.0"
    }
  }
}

provider "aws" {
  region   = "us-east-1"
  insecure = true
}


resource "aws_ecr_repository" "product_service" {
  name         = "gridshop-product-service"
  force_delete = true
}


data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Firewall for the public Application Load Balancer (allows HTTP port 80 from anywhere)
resource "aws_security_group" "alb_sg" {
  name        = "gridshop-alb-sg"
  description = "Allow public HTTP traffic to Load Balancer"
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
}

# Firewall for ECS Fargate containers (allows traffic on port 8082 from the ALB)
resource "aws_security_group" "ecs_sg" {
  name        = "gridshop-ecs-sg"
  description = "Allow traffic from ALB to product-service containers on port 8082"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 8082
    to_port         = 8082
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_lb" "app_alb" {
  name               = "gridshop-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = data.aws_subnets.default.ids
}

resource "aws_lb_target_group" "product_tg" {
  name        = "gridshop-product-tg"
  port        = 8082
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.default.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
    matcher             = "200"
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.app_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.product_tg.arn
  }
}


data "aws_iam_role" "ecs_task_execution_role" {
  name = "ecsTaskExecutionRole"
}


resource "aws_ecs_cluster" "main" {
  name = "gridshop-cluster"
}

resource "aws_ecs_task_definition" "product_task" {
  family                   = "gridshop-product-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = data.aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name      = "product-service"
      image     = "${aws_ecr_repository.product_service.repository_url}:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8082
          hostPort      = 8082
          protocol      = "tcp"
        }
      ]
      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:h2:mem:gridshop;DB_CLOSE_DELAY=-1;MODE=PostgreSQL" },
        { name = "SPRING_DATASOURCE_DRIVER_CLASS_NAME", value = "org.h2.Driver" },
        { name = "SPRING_DATASOURCE_USERNAME", value = "sa" },
        { name = "SPRING_DATASOURCE_PASSWORD", value = "" },
        { name = "EUREKA_CLIENT_ENABLED", value = "false" }
      ]
    }
  ])
}

resource "aws_ecs_service" "product_service" {
  name            = "gridshop-product-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.product_task.arn
  launch_type     = "FARGATE"
  desired_count   = 2 # Runs 2 containers in parallel behind the Load Balancer

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.ecs_sg.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.product_tg.arn
    container_name   = "product-service"
    container_port   = 8082
  }

  depends_on = [aws_lb_listener.http]
}


output "ecr_repository_url" {
  description = "URL of the ECR repository for Docker push"
  value       = aws_ecr_repository.product_service.repository_url
}

output "public_load_balancer_url" {
  description = "Public HTTP URL of the load-balanced service"
  value       = "http://${aws_lb.app_alb.dns_name}"
}
