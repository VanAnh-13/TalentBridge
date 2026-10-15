#!/bin/bash
# Update CORS settings for backend

gcloud run services update talentbridge \
  --region asia-southeast1 \
  --update-env-vars ALLOWED_ORIGINS="https://talentbridge-frontend-273039906673.asia-southeast1.run.app,https://talentbridge-frontend-ybp7znzbvq-as.a.run.app,http://localhost:3000,http://localhost:5173"
