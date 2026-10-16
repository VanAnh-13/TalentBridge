#!/bin/bash
# Simple TalentBridge deployment to Google Cloud Run: Cloud Build builds backend/Dockerfile and Cloud Run runs it.
# Settings come from the environment, see gcp-env.sh.
set -euo pipefail
source "$(dirname "$0")/gcp-env.sh"
require ALLOWED_ORIGINS FRONTEND_URL JWT_PRIVATE_KEY

echo "Deploying $SERVICE_NAME to Google Cloud Run in $PROJECT_ID ($REGION)..."

# Build and deploy in one command ("^@^" makes @ the list separator, so commas can appear in values)
gcloud run deploy "$SERVICE_NAME" \
    --project="$PROJECT_ID" \
    --source="$BUILD_CONTEXT" \
    --platform=managed \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=8080 \
    --memory=1Gi \
    --cpu=1 \
    --min-instances=0 \
    --max-instances=5 \
    --set-env-vars="^@^SPRING_PROFILES_ACTIVE=prod@STORAGE_TYPE=gcs@GCS_PROJECT_ID=$PROJECT_ID@GCS_BUCKET_NAME=$GCS_BUCKET_NAME@JWT_PRIVATE_KEY=$JWT_PRIVATE_KEY@ALLOWED_ORIGINS=$ALLOWED_ORIGINS@FRONTEND_URL=$FRONTEND_URL"

echo "Deployment complete!"
gcloud run services describe "$SERVICE_NAME" --project="$PROJECT_ID" --region="$REGION" --format="value(status.url)"
