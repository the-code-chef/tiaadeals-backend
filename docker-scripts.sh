#!/bin/bash

# Docker management scripts for TiaaDeals Backend
# Usage: ./docker-scripts.sh [command]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose and try again."
        exit 1
    fi
}

# Development environment commands
dev_start() {
    print_header "Starting Development Environment"
    check_docker
    check_docker_compose
    
    print_status "Starting development services..."
    docker-compose -f docker-compose.dev.yml up -d
    
    print_status "Waiting for services to be healthy..."
    sleep 10
    
    print_status "Development environment is ready!"
    echo -e "${GREEN}Services:${NC}"
    echo "  - Backend API: http://localhost:8080"
    echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  - Adminer (DB): http://localhost:8081"
    echo "  - Redis Commander: http://localhost:8082"
}

dev_stop() {
    print_header "Stopping Development Environment"
    docker-compose -f docker-compose.dev.yml down
    print_status "Development environment stopped."
}

dev_restart() {
    print_header "Restarting Development Environment"
    dev_stop
    dev_start
}

dev_logs() {
    print_header "Development Environment Logs"
    docker-compose -f docker-compose.dev.yml logs -f
}

dev_shell() {
    print_header "Opening Backend Shell"
    docker-compose -f docker-compose.dev.yml exec backend /bin/bash
}

# Production environment commands
prod_start() {
    print_header "Starting Production Environment"
    check_docker
    check_docker_compose
    
    if [ ! -f ".env" ]; then
        print_warning "No .env file found. Creating from template..."
        cp env.example .env
        print_warning "Please edit .env file with your production values before starting."
        exit 1
    fi
    
    print_status "Starting production services..."
    docker-compose -f docker-compose.prod.yml up -d
    
    print_status "Waiting for services to be healthy..."
    sleep 15
    
    print_status "Production environment is ready!"
    echo -e "${GREEN}Services:${NC}"
    echo "  - Backend API: http://localhost:8080"
    echo "  - Nginx Proxy: http://localhost:80"
}

prod_stop() {
    print_header "Stopping Production Environment"
    docker-compose -f docker-compose.prod.yml down
    print_status "Production environment stopped."
}

prod_restart() {
    print_header "Restarting Production Environment"
    prod_stop
    prod_start
}

prod_logs() {
    print_header "Production Environment Logs"
    docker-compose -f docker-compose.prod.yml logs -f
}

# Utility commands
status() {
    print_header "Service Status"
    echo -e "${GREEN}Development Environment:${NC}"
    docker-compose -f docker-compose.dev.yml ps 2>/dev/null || echo "  Not running"
    echo ""
    echo -e "${GREEN}Production Environment:${NC}"
    docker-compose -f docker-compose.prod.yml ps 2>/dev/null || echo "  Not running"
}

cleanup() {
    print_header "Cleaning Up Docker Resources"
    print_warning "This will remove all containers, networks, and volumes!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_status "Stopping all containers..."
        docker-compose -f docker-compose.dev.yml down -v 2>/dev/null || true
        docker-compose -f docker-compose.prod.yml down -v 2>/dev/null || true
        
        print_status "Removing unused containers..."
        docker container prune -f
        
        print_status "Removing unused networks..."
        docker network prune -f
        
        print_status "Removing unused volumes..."
        docker volume prune -f
        
        print_status "Removing unused images..."
        docker image prune -f
        
        print_status "Cleanup completed!"
    else
        print_status "Cleanup cancelled."
    fi
}

build() {
    print_header "Building Docker Images"
    print_status "Building development image..."
    docker-compose -f docker-compose.dev.yml build
    
    print_status "Building production image..."
    docker-compose -f docker-compose.prod.yml build
    
    print_status "Build completed!"
}

health() {
    print_header "Health Checks"
    
    # Check if services are running
    if docker-compose -f docker-compose.dev.yml ps | grep -q "Up"; then
        print_status "Development environment is running"
        echo "  - Backend health: $(curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "unavailable")"
    else
        print_warning "Development environment is not running"
    fi
    
    if docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
        print_status "Production environment is running"
        echo "  - Backend health: $(curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "unavailable")"
        echo "  - Nginx health: $(curl -s http://localhost/health || echo "unavailable")"
    else
        print_warning "Production environment is not running"
    fi
}

backup() {
    print_header "Creating Database Backup"
    timestamp=$(date +%Y%m%d_%H%M%S)
    backup_file="backup_${timestamp}.sql"
    
    if docker-compose -f docker-compose.dev.yml ps | grep -q "Up"; then
        print_status "Creating backup from development database..."
        docker-compose -f docker-compose.dev.yml exec -T postgres pg_dump -U postgres tiaadeals > "$backup_file"
    elif docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
        print_status "Creating backup from production database..."
        docker-compose -f docker-compose.prod.yml exec -T postgres pg_dump -U postgres tiaadeals > "$backup_file"
    else
        print_error "No running database found. Please start the environment first."
        exit 1
    fi
    
    print_status "Backup created: $backup_file"
}

help() {
    print_header "Docker Scripts Help"
    echo "Usage: $0 [command]"
    echo ""
    echo "Development Commands:"
    echo "  dev-start    - Start development environment"
    echo "  dev-stop     - Stop development environment"
    echo "  dev-restart  - Restart development environment"
    echo "  dev-logs     - Show development logs"
    echo "  dev-shell    - Open shell in backend container"
    echo ""
    echo "Production Commands:"
    echo "  prod-start   - Start production environment"
    echo "  prod-stop    - Stop production environment"
    echo "  prod-restart - Restart production environment"
    echo "  prod-logs    - Show production logs"
    echo ""
    echo "Utility Commands:"
    echo "  status       - Show service status"
    echo "  build        - Build Docker images"
    echo "  health       - Check service health"
    echo "  backup       - Create database backup"
    echo "  cleanup      - Clean up Docker resources"
    echo "  help         - Show this help message"
}

# Main script logic
case "${1:-help}" in
    # Development commands
    "dev-start"|"devstart")
        dev_start
        ;;
    "dev-stop"|"devstop")
        dev_stop
        ;;
    "dev-restart"|"devrestart")
        dev_restart
        ;;
    "dev-logs"|"devlogs")
        dev_logs
        ;;
    "dev-shell"|"devshell")
        dev_shell
        ;;
    
    # Production commands
    "prod-start"|"prodstart")
        prod_start
        ;;
    "prod-stop"|"prodstop")
        prod_stop
        ;;
    "prod-restart"|"prodrestart")
        prod_restart
        ;;
    "prod-logs"|"prodlogs")
        prod_logs
        ;;
    
    # Utility commands
    "status")
        status
        ;;
    "build")
        build
        ;;
    "health")
        health
        ;;
    "backup")
        backup
        ;;
    "cleanup")
        cleanup
        ;;
    "help"|"--help"|"-h")
        help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        help
        exit 1
        ;;
esac 