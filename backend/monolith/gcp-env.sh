#!/bin/bash
# Settings shared by the Cloud Run scripts (sourced, not run). Nothing about a particular installation is written
# here: it all comes from the environment.
#
#   PROJECT_ID        GCP project                         (default: the active gcloud project)
#   REGION            Cloud Run / Artifact Registry region (default: asia-southeast1)
#   SERVICE_NAME      Cloud Run service and image name    (default: talentbridge)
#   GCS_BUCKET_NAME   bucket for uploaded files           (default: <SERVICE_NAME>-files)
#   ALLOWED_ORIGINS   web origins allowed to call the API, comma separated (required to deploy)
#   FRONTEND_URL      public address of the web app; links in e-mails point there (required to deploy)
#   JWT_PRIVATE_KEY   RSA key that signs access tokens, base64 of the PEM (required to deploy):
#                       openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 | base64 -w0

PROJECT_ID="${PROJECT_ID:-$(gcloud config get-value project 2>/dev/null)}"
REGION="${REGION:-asia-southeast1}"
SERVICE_NAME="${SERVICE_NAME:-talentbridge}"
GCS_BUCKET_NAME="${GCS_BUCKET_NAME:-$SERVICE_NAME-files}"
# backend/ is the build context: backend/Dockerfile builds every module (the monolith by default)
BUILD_CONTEXT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Stops the script when one of the named variables is empty.
require() {
    local name
    for name in "$@"; do
        if [ -z "${!name:-}" ]; then
            echo "Error: set $name (see backend/monolith/gcp-env.sh)" >&2
            exit 1
        fi
    done
}

require PROJECT_ID
