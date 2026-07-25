#!/bin/bash

set -euo pipefail

NAMESPACE="event-platform"
OVERLAY="k8s/overlays/dev"

echo "🚀 Deploying Event Platform..."

kubectl apply -k "$OVERLAY"

echo ""
echo "⏳ Waiting for deployments..."

SERVICES=(
  api-gateway
  auth-service
  config-server
  discovery-server
  order-service
  inventory-service
  pricing-service
  payment-service
  notification-service
  delivery-service
  websocket-service
  saga-orchestrator
)

for service in "${SERVICES[@]}"; do
  echo "Waiting for $service..."
  kubectl rollout status deployment/app-"$service" \
    -n "$NAMESPACE" \
    --timeout=5m
done

echo ""
echo "✅ Deployment completed successfully."