#!/bin/bash

# TalentBridge - Deploy to Google Cloud Run
# This script builds and deploys your application to GCP

set -e

PROJECT_ID="talentbridge-storage"
REGION="asia-southeast1"
SERVICE_NAME="talentbridge"

echo "=========================================="
echo "TalentBridge - Google Cloud Deployment"
echo "=========================================="

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo "Error: gcloud CLI is not installed"
    exit 1
fi

# Set project
echo "Setting project to $PROJECT_ID..."
gcloud config set project $PROJECT_ID

# Generate JWT secret if not set
if [ -z "$JWT_SECRET" ]; then
    echo "Generating JWT secret..."
    export JWT_SECRET=$(openssl rand -base64 64)
    echo "JWT_SECRET generated (save this for future deployments)"
fi

echo ""
echo "Building and deploying to Cloud Run..."
echo "This may take 5-10 minutes..."
echo ""

# Deploy using source-based deployment (Cloud Build will handle the build)
gcloud run deploy $SERVICE_NAME \
    --source . \
    --platform=managed \
    --region=$REGION \
    --allow-unauthenticated \
    --port=8080 \
    --memory=1Gi \
    --cpu=1 \
    --timeout=300 \
    --min-instances=0 \
    --max-instances=5 \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod,STORAGE_TYPE=gcs,GCS_PROJECT_ID=$PROJECT_ID,GCS_BUCKET_NAME=talentbridge-files,JWT_SECRET=$JWT_SECRET,JWT_ACCESS_TOKEN_EXPIRATION=900,JWT_REFRESH_TOKEN_EXPIRATION=86400,SPRING_DATASOURCE_URL=jdbc:h2:mem:talentbridge,SPRING_DATASOURCE_USERNAME=sa,SPRING_DATASOURCE_PASSWORD=,SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver,SPRING_CACHE_TYPE=simple"

# Get the service URL
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.url)")

echo ""
echo "=========================================="
echo "Deployment Successful!"
echo "=========================================="
echo "Service URL: $SERVICE_URL"
echo "API Docs: $SERVICE_URL/swagger-ui.html"
echo "Health Check: $SERVICE_URL/actuator/health"
echo ""
echo "Your JWT_SECRET: $JWT_SECRET"
echo "(Save this for future reference)"
echo "=========================================="
