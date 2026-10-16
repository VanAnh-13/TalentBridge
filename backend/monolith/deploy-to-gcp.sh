#!/bin/bash

# TalentBridge - Deploy to Google Cloud Run (demo setup: in-memory H2 database, files in Cloud Storage)
# Builds backend/Dockerfile with Cloud Build and deploys it. Settings come from the environment, see gcp-env.sh.

set -euo pipefail
source "$(dirname "$0")/gcp-env.sh"
require ALLOWED_ORIGINS FRONTEND_URL JWT_PRIVATE_KEY

echo "=========================================="
echo "TalentBridge - Google Cloud Deployment"
echo "=========================================="

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo "Error: gcloud CLI is not installed"
    exit 1
fi

echo ""
echo "Building and deploying $SERVICE_NAME to Cloud Run in $PROJECT_ID ($REGION)..."
echo "This may take 5-10 minutes..."
echo ""

# Deploy using source-based deployment (Cloud Build will handle the build).
# "^@^" makes @ the list separator, so commas can appear in values.
gcloud run deploy "$SERVICE_NAME" \
    --project="$PROJECT_ID" \
    --source="$BUILD_CONTEXT" \
    --platform=managed \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=8080 \
    --memory=1Gi \
    --cpu=1 \
    --timeout=300 \
    --min-instances=0 \
    --max-instances=5 \
    --set-env-vars="^@^SPRING_PROFILES_ACTIVE=prod@STORAGE_TYPE=gcs@GCS_PROJECT_ID=$PROJECT_ID@GCS_BUCKET_NAME=$GCS_BUCKET_NAME@JWT_PRIVATE_KEY=$JWT_PRIVATE_KEY@JWT_ACCESS_TOKEN_EXPIRATION=900@JWT_REFRESH_TOKEN_EXPIRATION=86400@ALLOWED_ORIGINS=$ALLOWED_ORIGINS@FRONTEND_URL=$FRONTEND_URL@SPRING_DATASOURCE_URL=jdbc:h2:mem:talentbridge@SPRING_DATASOURCE_USERNAME=sa@SPRING_DATASOURCE_PASSWORD=@SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver@SPRING_CACHE_TYPE=simple"

# Get the service URL
SERVICE_URL=$(gcloud run services describe "$SERVICE_NAME" --project="$PROJECT_ID" --region="$REGION" --format="value(status.url)")

echo ""
echo "=========================================="
echo "Deployment Successful!"
echo "=========================================="
echo "Service URL: $SERVICE_URL"
echo "API Docs: $SERVICE_URL/swagger-ui.html"
echo "Health Check: $SERVICE_URL/actuator/health"
echo "=========================================="
