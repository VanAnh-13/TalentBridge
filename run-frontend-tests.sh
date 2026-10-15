#!/bin/bash

# Script to run frontend tests for TalentBridge

echo "========================================"
echo "Starting TalentBridge Frontend Tests"
echo "========================================"

cd frontend

# Install dependencies if not already installed
echo "Installing dependencies..."
npm install
npm install --save-dev cypress @cypress/react cypress-file-upload

# Run Cypress tests
echo "Running Cypress E2E tests..."
npx cypress run --config video=false

# Run component tests if any
echo "Running component tests..."
npm run test:unit --if-present

echo "========================================"
echo "Frontend test execution completed!"
echo "========================================"
echo "Test results available at:"
echo "- cypress/screenshots/ (for failed tests)"
echo "- cypress/results/"
