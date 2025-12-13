#!/bin/bash

# TalentBridge Frontend Google Cloud Deployment Script
set -e

# Configuration
PROJECT_ID="talentbridge-storage"
REGION="asia-southeast1"
APP_NAME="talentbridge-frontend"
REPO_NAME="talentbridge" # Reuse same repo
BACKEND_URL="https://talentbridge-273039906673.asia-southeast1.run.app"

echo "=========================================="
echo "TalentBridge Frontend GCP Deployment"
echo "=========================================="

# Set project
echo "Setting project to $PROJECT_ID..."
gcloud config set project $PROJECT_ID

# Build and push Docker image
echo "Building Docker image..."
IMAGE_URL="$REGION-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/$APP_NAME:latest"
gcloud builds submit --config cloudbuild.yaml --substitutions=_VITE_API_URL=$BACKEND_URL

# Deploy to Cloud Run
echo "Deploying to Cloud Run..."
gcloud run deploy $APP_NAME \
    --image=$IMAGE_URL \
    --platform=managed \
    --region=$REGION \
    --allow-unauthenticated \
    --port=8080 \
    --memory=512Mi \
    --cpu=1 \
    --min-instances=0 \
    --max-instances=10

# Get service URL
SERVICE_URL=$(gcloud run services describe $APP_NAME --region=$REGION --format="value(status.url)")

echo "=========================================="
echo "Deployment Complete!"
echo "=========================================="
echo "Frontend Service URL: $SERVICE_URL"
echo "Backend Service URL: $BACKEND_URL"
echo "=========================================="
