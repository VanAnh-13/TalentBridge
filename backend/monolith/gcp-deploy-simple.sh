#!/bin/bash

# Simple TalentBridge Google Cloud Run Deployment
set -e

PROJECT_ID="talentbridge-storage"
REGION="asia-southeast1"
APP_NAME="talentbridge"

echo "Deploying TalentBridge to Google Cloud Run..."

# Set project
gcloud config set project $PROJECT_ID

# Build and deploy in one command
gcloud run deploy $APP_NAME \
    --source . \
    --platform=managed \
    --region=$REGION \
    --allow-unauthenticated \
    --port=8080 \
    --memory=1Gi \
    --cpu=1 \
    --min-instances=0 \
    --max-instances=5 \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars="GCS_PROJECT_ID=$PROJECT_ID" \
    --set-env-vars="GCS_BUCKET_NAME=talentbridge-files" \
    --set-env-vars="JWT_SECRET=${JWT_SECRET:-$(openssl rand -base64 64)}"

echo "Deployment complete!"
gcloud run services describe $APP_NAME --region=$REGION --format="value(status.url)"
