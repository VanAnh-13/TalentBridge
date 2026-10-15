#!/bin/bash

# TalentBridge Google Cloud Deployment Script
set -e

# Configuration
PROJECT_ID="talentbridge-storage"
REGION="asia-southeast1"
APP_NAME="talentbridge"
DB_INSTANCE_NAME="talentbridge-db"
REDIS_INSTANCE_NAME="talentbridge-redis"

echo "=========================================="
echo "TalentBridge GCP Deployment"
echo "=========================================="

# Set project
echo "Setting project to $PROJECT_ID..."
gcloud config set project $PROJECT_ID

# Create Artifact Registry repository for Docker images
echo "Creating Artifact Registry repository..."
gcloud artifacts repositories create $APP_NAME \
    --repository-format=docker \
    --location=$REGION \
    --description="TalentBridge Docker repository" \
    2>/dev/null || echo "Repository already exists"

# Build and push Docker image
echo "Building Docker image..."
IMAGE_URL="$REGION-docker.pkg.dev/$PROJECT_ID/$APP_NAME/$APP_NAME:latest"
gcloud builds submit --tag $IMAGE_URL

# Create Cloud SQL instance (MariaDB)
echo "Creating Cloud SQL instance..."
gcloud sql instances create $DB_INSTANCE_NAME \
    --database-version=MYSQL_8_0 \
    --tier=db-f1-micro \
    --region=$REGION \
    --root-password="${DB_ROOT_PASSWORD:-$(openssl rand -base64 32)}" \
    --storage-type=SSD \
    --storage-size=10GB \
    --backup \
    --backup-start-time=03:00 \
    2>/dev/null || echo "SQL instance already exists"

# Create database
echo "Creating database..."
gcloud sql databases create talentbridge \
    --instance=$DB_INSTANCE_NAME \
    2>/dev/null || echo "Database already exists"

# Create database user
echo "Creating database user..."
DB_PASSWORD="${DB_PASSWORD:-$(openssl rand -base64 32)}"
gcloud sql users create talentbridge_user \
    --instance=$DB_INSTANCE_NAME \
    --password="$DB_PASSWORD" \
    2>/dev/null || echo "User already exists"

# Create Redis instance
echo "Creating Redis instance..."
gcloud redis instances create $REDIS_INSTANCE_NAME \
    --size=1 \
    --region=$REGION \
    --redis-version=redis_7_0 \
    --tier=basic \
    2>/dev/null || echo "Redis instance already exists"

# Get Redis host
REDIS_HOST=$(gcloud redis instances describe $REDIS_INSTANCE_NAME --region=$REGION --format="value(host)")
REDIS_PORT=$(gcloud redis instances describe $REDIS_INSTANCE_NAME --region=$REGION --format="value(port)")

# Get Cloud SQL connection name
SQL_CONNECTION=$(gcloud sql instances describe $DB_INSTANCE_NAME --format="value(connectionName)")

# Deploy to Cloud Run
echo "Deploying to Cloud Run..."
gcloud run deploy $APP_NAME \
    --image=$IMAGE_URL \
    --platform=managed \
    --region=$REGION \
    --allow-unauthenticated \
    --port=8080 \
    --memory=1Gi \
    --cpu=1 \
    --min-instances=0 \
    --max-instances=10 \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars="SPRING_DATASOURCE_URL=jdbc:mysql:///$DB_NAME?cloudSqlInstance=$SQL_CONNECTION&socketFactory=com.google.cloud.sql.mysql.SocketFactory" \
    --set-env-vars="SPRING_DATASOURCE_USERNAME=talentbridge_user" \
    --set-env-vars="SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD" \
    --set-env-vars="REDIS_HOST=$REDIS_HOST" \
    --set-env-vars="REDIS_PORT=$REDIS_PORT" \
    --set-env-vars="SPRING_CACHE_TYPE=redis" \
    --set-env-vars="JWT_SECRET=${JWT_SECRET}" \
    --set-env-vars="JWT_ACCESS_TOKEN_EXPIRATION=900" \
    --set-env-vars="JWT_REFRESH_TOKEN_EXPIRATION=86400" \
    --set-env-vars="GCS_PROJECT_ID=$PROJECT_ID" \
    --set-env-vars="GCS_BUCKET_NAME=talentbridge-files" \
    --set-env-vars="GCS_CREDENTIALS_PATH=/app/gcs-credentials.json" \
    --add-cloudsql-instances=$SQL_CONNECTION

# Get service URL
SERVICE_URL=$(gcloud run services describe $APP_NAME --region=$REGION --format="value(status.url)")

echo "=========================================="
echo "Deployment Complete!"
echo "=========================================="
echo "Service URL: $SERVICE_URL"
echo "Cloud SQL Instance: $DB_INSTANCE_NAME"
echo "Redis Instance: $REDIS_INSTANCE_NAME"
echo "GCS Bucket: talentbridge-files"
echo "=========================================="
