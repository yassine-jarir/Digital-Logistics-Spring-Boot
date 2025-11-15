#!/bin/bash

# Jenkins Docker Installation Script
# Digital Logistics CI/CD Setup

echo "🚀 Installing Jenkins with Docker..."

# Create Jenkins volume for persistent data
docker volume create jenkins_home

# Run Jenkins container
docker run -d \
  --name jenkins \
  -p 8081:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

echo "⏳ Waiting for Jenkins to start (this may take 1-2 minutes)..."
sleep 30

# Get the initial admin password
echo ""
echo "=========================================="
echo "🔑 JENKINS INITIAL ADMIN PASSWORD:"
echo "=========================================="
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
echo "=========================================="
echo ""
echo "✅ Jenkins is starting at: http://localhost:8081"
echo "📋 Copy the password above and paste it in Jenkins setup wizard"
echo ""
echo "To see the password again later, run:"
echo "  docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
echo ""
echo "To view Jenkins logs:"
echo "  docker logs jenkins -f"

