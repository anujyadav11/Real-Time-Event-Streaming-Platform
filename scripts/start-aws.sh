#!/bin/bash

set -e

CLUSTER_NAME="eventstream-prod"
NODEGROUP_NAME="eventstream-workers"
REGION="ap-south-1"

echo "Starting EKS worker node..."

aws eks update-nodegroup-config \
  --cluster-name "$CLUSTER_NAME" \
  --nodegroup-name "$NODEGROUP_NAME" \
  --scaling-config minSize=1,maxSize=2,desiredSize=2 \
  --region "$REGION"

echo "Waiting for node..."

aws eks wait nodegroup-active \
  --cluster-name "$CLUSTER_NAME" \
  --nodegroup-name "$NODEGROUP_NAME" \
  --region "$REGION"

echo "Updating kubeconfig..."

aws eks update-kubeconfig \
  --name "$CLUSTER_NAME" \
  --region "$REGION"

echo "Checking cluster..."

kubectl get nodes

echo "AWS environment is ready."