#!/bin/bash
# Changes the web origins allowed by CORS on the deployed backend, e.g.
#   ALLOWED_ORIGINS="https://app.example.com,http://localhost:3000" ./update-cors.sh
set -euo pipefail
source "$(dirname "$0")/gcp-env.sh"
require ALLOWED_ORIGINS

# "^@^" makes @ the list separator, so the commas stay inside the value
gcloud run services update "$SERVICE_NAME" \
  --project "$PROJECT_ID" \
  --region "$REGION" \
  --update-env-vars "^@^ALLOWED_ORIGINS=$ALLOWED_ORIGINS"
