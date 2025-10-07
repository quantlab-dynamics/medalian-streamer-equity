#!/bin/bash

# Define the base deployment directory
DEPLOYMENT_ROOT="/opt/codedeploy-agent/deployment-root"

# Check if there are any directories inside $DEPLOYMENT_ROOT
echo "Listing directories in $DEPLOYMENT_ROOT:"

# Find the most recent directory with a valid format (e.g., alphanumeric directory names)
DEPLOYMENT_BASE=$(ls -1t $DEPLOYMENT_ROOT | grep -E '^[0-9a-f-]+$' | head -n 1)
DEPLOYMENT_BASE_ROOT=$DEPLOYMENT_ROOT/$DEPLOYMENT_BASE
DEPLOYMENT_BASE_SECOND=$(ls -1t $DEPLOYMENT_BASE_ROOT | grep -E '^d-[0-9A-Z-]+$' | head -n 1)
LATEST_DEPLOYMENT_DIR=$DEPLOYMENT_BASE_ROOT/$DEPLOYMENT_BASE_SECOND

# Check if we have a deployment directory
if [ -z "$LATEST_DEPLOYMENT_DIR" ]; then
    echo "Error: No deployment directory found in $DEPLOYMENT_ROOT"
    exit 1
fi

# Define the full path to the deployment archive directory
DEPLOYMENT_DIR="$LATEST_DEPLOYMENT_DIR/deployment-archive"

# Ensure the deployment directory exists
if [ ! -d "$DEPLOYMENT_DIR" ]; then
    echo "Error: Deployment directory $DEPLOYMENT_DIR not found."
    exit 1
fi

# Print the deployment directory for debugging
echo "Using deployment directory: $DEPLOYMENT_DIR"

# Check if docker-compose.yml exists
if [ ! -f "$DEPLOYMENT_DIR/docker-compose.yml" ]; then
    echo "Error: docker-compose.yml not found in $DEPLOYMENT_DIR"
    exit 1
fi

# Navigate to the deployment directory
cd "$DEPLOYMENT_DIR" || exit

# Rebuild and start the containers using docker-compose
echo "Rebuilding and starting containers..."
docker-compose -f "$DEPLOYMENT_DIR/docker-compose.yml" up --build -d

