#!/bin/bash

# Build and test Docker images locally
echo "Building Docker images..."

# Build backend
echo "Building backend..."
docker build -t bagnsave-backend:latest ./backend

# Build frontend
echo "Building frontend..."
docker build -t bagnsave-frontend:latest ./frontend

echo "Images built successfully!"
echo "To run locally: docker-compose up -d"
echo "To stop: docker-compose down"
echo "Frontend will be available at http://localhost"
echo "Backend API at http://localhost:8080/api"

