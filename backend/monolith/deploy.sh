#!/bin/bash

# TalentBridge Deployment Script
# This script helps deploy the TalentBridge application using Docker Compose

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Function to print colored messages
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if .env file exists
check_env_file() {
    if [ ! -f .env ]; then
        print_warning ".env file not found!"
        print_message "Creating .env from .env.example..."
        cp .env.example .env
        print_warning "Please edit .env file with your configuration before deploying!"
        print_warning "At minimum, update:"
        echo "  - Database passwords"
        echo "  - Redis password"
        echo "  - JWT secret"
        read -p "Press Enter after updating .env file to continue..."
    fi
}

# Function to check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
}

# Function to build the application
build_app() {
    print_message "Building TalentBridge application..."
    docker compose -f docker-compose.prod.yml build
    print_message "Build completed successfully!"
}

# Function to start services
start_services() {
    print_message "Starting TalentBridge services..."
    docker compose -f docker-compose.prod.yml up -d
    print_message "Services started!"
    print_message "Waiting for services to be healthy..."
    sleep 10
    docker compose -f docker-compose.prod.yml ps
}

# Function to stop services
stop_services() {
    print_message "Stopping TalentBridge services..."
    docker compose -f docker-compose.prod.yml down
    print_message "Services stopped!"
}

# Function to restart services
restart_services() {
    print_message "Restarting TalentBridge services..."
    docker compose -f docker-compose.prod.yml restart
    print_message "Services restarted!"
}

# Function to view logs
view_logs() {
    local service=$1
    if [ -z "$service" ]; then
        docker compose -f docker-compose.prod.yml logs -f
    else
        docker compose -f docker-compose.prod.yml logs -f "$service"
    fi
}

# Function to show status
show_status() {
    print_message "TalentBridge Services Status:"
    docker compose -f docker-compose.prod.yml ps
}

# Function to clean up
cleanup() {
    print_warning "This will remove all containers, networks, and volumes!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_message "Cleaning up..."
        docker compose -f docker-compose.prod.yml down -v
        print_message "Cleanup completed!"
    else
        print_message "Cleanup cancelled."
    fi
}

# Function to backup database
backup_database() {
    print_message "Creating database backup..."
    BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
    docker compose -f docker-compose.prod.yml exec -T mariadb mysqldump -u root -p"${DB_ROOT_PASSWORD}" "${DB_NAME}" > "$BACKUP_FILE"
    print_message "Database backup created: $BACKUP_FILE"
}

# Function to show help
show_help() {
    cat << EOF
TalentBridge Deployment Script

Usage: ./deploy.sh [COMMAND]

Commands:
  build       Build the application Docker image
  start       Start all services
  stop        Stop all services
  restart     Restart all services
  logs        View logs (add service name to view specific service)
  status      Show status of all services
  backup      Backup database
  clean       Remove all containers, networks, and volumes
  help        Show this help message

Examples:
  ./deploy.sh build
  ./deploy.sh start
  ./deploy.sh logs app
  ./deploy.sh backup

EOF
}

# Main script logic
main() {
    local command=$1

    # Check prerequisites
    check_docker
    check_env_file

    # Load environment variables
    source .env

    case $command in
        build)
            build_app
            ;;
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        logs)
            view_logs "$2"
            ;;
        status)
            show_status
            ;;
        backup)
            backup_database
            ;;
        clean)
            cleanup
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown command: $command"
            show_help
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
