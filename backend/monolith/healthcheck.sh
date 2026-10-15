#!/bin/bash

# TalentBridge Health Check Script
# This script checks the health of all services

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
APP_URL="${APP_URL:-http://localhost:8080}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
REDIS_HOST="${REDIS_HOST:-localhost}"
REDIS_PORT="${REDIS_PORT:-6379}"

print_status() {
    local service=$1
    local status=$2
    if [ "$status" = "healthy" ]; then
        echo -e "${GREEN}✓${NC} $service: ${GREEN}Healthy${NC}"
        return 0
    else
        echo -e "${RED}✗${NC} $service: ${RED}Unhealthy${NC}"
        return 1
    fi
}

check_application() {
    echo "Checking Application..."
    if curl -f -s "${APP_URL}/actuator/health" > /dev/null 2>&1; then
        local health_status=$(curl -s "${APP_URL}/actuator/health" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        if [ "$health_status" = "UP" ]; then
            print_status "Application" "healthy"
            return 0
        fi
    fi
    print_status "Application" "unhealthy"
    return 1
}

check_database() {
    echo "Checking Database..."
    if command -v mysqladmin > /dev/null 2>&1; then
        if mysqladmin ping -h "$DB_HOST" -P "$DB_PORT" > /dev/null 2>&1; then
            print_status "Database" "healthy"
            return 0
        fi
    elif command -v nc > /dev/null 2>&1; then
        if nc -zv "$DB_HOST" "$DB_PORT" > /dev/null 2>&1; then
            print_status "Database" "healthy"
            return 0
        fi
    fi
    print_status "Database" "unhealthy"
    return 1
}

check_redis() {
    echo "Checking Redis..."
    if command -v redis-cli > /dev/null 2>&1; then
        if redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ping > /dev/null 2>&1; then
            print_status "Redis" "healthy"
            return 0
        fi
    elif command -v nc > /dev/null 2>&1; then
        if nc -zv "$REDIS_HOST" "$REDIS_PORT" > /dev/null 2>&1; then
            print_status "Redis" "healthy"
            return 0
        fi
    fi
    print_status "Redis" "unhealthy"
    return 1
}

check_disk_space() {
    echo "Checking Disk Space..."
    local usage=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
    if [ "$usage" -lt 80 ]; then
        print_status "Disk Space (${usage}% used)" "healthy"
        return 0
    elif [ "$usage" -lt 90 ]; then
        echo -e "${YELLOW}⚠${NC} Disk Space: ${YELLOW}Warning${NC} (${usage}% used)"
        return 0
    else
        print_status "Disk Space (${usage}% used)" "unhealthy"
        return 1
    fi
}

check_memory() {
    echo "Checking Memory..."
    if command -v free > /dev/null 2>&1; then
        local mem_usage=$(free | awk 'NR==2 {printf "%.0f", $3/$2 * 100}')
        if [ "$mem_usage" -lt 80 ]; then
            print_status "Memory (${mem_usage}% used)" "healthy"
            return 0
        elif [ "$mem_usage" -lt 90 ]; then
            echo -e "${YELLOW}⚠${NC} Memory: ${YELLOW}Warning${NC} (${mem_usage}% used)"
            return 0
        else
            print_status "Memory (${mem_usage}% used)" "unhealthy"
            return 1
        fi
    fi
    return 0
}

# Main execution
main() {
    echo "=================================="
    echo "TalentBridge Health Check"
    echo "=================================="
    echo ""

    local all_healthy=true

    check_application || all_healthy=false
    check_database || all_healthy=false
    check_redis || all_healthy=false
    check_disk_space || all_healthy=false
    check_memory || all_healthy=false

    echo ""
    echo "=================================="
    
    if [ "$all_healthy" = true ]; then
        echo -e "${GREEN}All services are healthy${NC}"
        exit 0
    else
        echo -e "${RED}Some services are unhealthy${NC}"
        exit 1
    fi
}

main
