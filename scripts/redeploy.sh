#!/usr/bin/env bash

set -Eeuo pipefail

kubectl rollout restart deployment -n event-platform

kubectl rollout status deployment/app-config-server -n event-platform
kubectl rollout status deployment/app-discovery-server -n event-platform
kubectl rollout status deployment/app-api-gateway -n event-platform

kubectl get pods -n event-platform