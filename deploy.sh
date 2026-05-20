#!/usr/bin/env bash
set -euo pipefail

APP_NAME="zaocycle"
RG_NAME="zaocycle"

echo "Building JAR..."
./gradlew clean bootJar -x test

echo "Renaming for Azure..."
cp build/libs/zaocyclebackend-*.jar build/libs/app.jar

echo "Deploying..."
az webapp deploy \
  --name "$APP_NAME" \
  --resource-group "$RG_NAME" \
  --src-path build/libs/app.jar \
  --type jar

echo "Done. Tailing logs (Ctrl+C to exit)..."
az webapp log tail --name "$APP_NAME" --resource-group "$RG_NAME"