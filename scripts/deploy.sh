#!/usr/bin/env bash

set -Eeuo pipefail

NAMESPACE="event-platform"
OVERLAY="k8s/overlays/docker-desktop"

SERVICES=(
  config-server
  discovery-server
  auth-service
  api-gateway
  order-service
  inventory-service
  pricing-service
  payment-service
  notification-service
  delivery-service
  websocket-service
  saga-orchestrator
)

echo "🚀 Deploying Event Platform..."

kubectl apply -k "$OVERLAY"

echo ""
echo "⏳ Waiting for deployments..."

for service in "${SERVICES[@]}"; do
    echo "▶ Waiting for app-$service..."

    kubectl rollout status \
        deployment/app-"$service" \
        -n "$NAMESPACE" \
        --timeout=5m
done

echo ""
echo "📦 Current Pods"
kubectl get pods -n "$NAMESPACE"

echo ""
echo "📦 Current Services"
kubectl get svc -n "$NAMESPACE"

echo ""
echo "🎉 Event Platform deployed successfully."