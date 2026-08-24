#!/bin/bash

set -e

CLUSTER_NAME="eventstream-prod"
NODEGROUP_NAME="eventstream-workers"
REGION="ap-south-1"

echo "Stopping EKS worker nodes..."

aws eks update-nodegroup-config \
  --cluster-name "$CLUSTER_NAME" \
  --nodegroup-name "$NODEGROUP_NAME" \
  --scaling-config minSize=0,maxSize=1,desiredSize=0 \
  --region "$REGION"

echo "Waiting for node group..."

aws eks wait nodegroup-active \
  --cluster-name "$CLUSTER_NAME" \
  --nodegroup-name "$NODEGROUP_NAME" \
  --region "$REGION"

echo ""
echo "Worker nodes scaled to 0."
echo "EKS control plane still exists."
echo "Worker EC2 costs have been stopped."