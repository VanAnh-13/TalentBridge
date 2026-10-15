#!/bin/bash

# Script to run backend tests for TalentBridge

echo "========================================"
echo "Starting TalentBridge Backend Tests"
echo "========================================"

cd backend

# Clean and build
echo "Building project..."
./mvnw clean compile

# Run tests with coverage
echo "Running tests..."
./mvnw test -Dspring.profiles.active=test

# Generate test report
echo "Generating test report..."
./mvnw surefire-report:report

echo "========================================"
echo "Test execution completed!"
echo "========================================"
echo "Test reports available at:"
echo "- target/surefire-reports/"
echo "- target/site/surefire-report.html"
