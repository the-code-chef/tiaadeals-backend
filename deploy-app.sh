#!/bin/bash

# Exit on error
set -e

# Configuration
REMOTE_USER="ubuntu"
REMOTE_HOST="tiaadeals.com"
APP_DIR="/var/www/tiaadeals-backend"

echo "Starting application deployment..."

# Build the application
echo "Building application..."
npm run build

# Create deployment package
echo "Creating deployment package..."
tar -czf deploy.tar.gz \
    --exclude="node_modules" \
    --exclude=".git" \
    --exclude="deploy.tar.gz" \
    --exclude=".env" \
    --exclude=".env.*" \
    .

# Copy files to server
echo "Copying files to server..."
scp -i ~/.ssh/tiaadeals.pem deploy.tar.gz $REMOTE_USER@$REMOTE_HOST:$APP_DIR/

# Deploy on server
echo "Deploying on server..."
ssh -i ~/.ssh/tiaadeals.pem $REMOTE_USER@$REMOTE_HOST << 'EOF'
    cd /var/www/tiaadeals-backend
    
    # Backup current version
    if [ -d "current" ]; then
        mv current "backup_$(date +%Y%m%d_%H%M%S)"
    fi
    
    # Extract new version
    mkdir -p current
    tar -xzf deploy.tar.gz -C current
    
    # Install dependencies
    cd current
    npm install --production
    
    # Copy environment file
    cp ../.env.production .env
    
    # Restart application
    pm2 reload tiaadeals-backend
    
    # Verify SSL certificates
    sudo certbot certificates
    
    # Renew SSL certificates if needed
    sudo certbot renew --dry-run
    
    # Restart Nginx to ensure all configurations are loaded
    sudo systemctl restart nginx
    
    # Cleanup
    cd ..
    rm deploy.tar.gz
EOF

# Cleanup local files
echo "Cleaning up..."
rm deploy.tar.gz

echo "Deployment completed successfully!"
echo "Your application is now accessible at:"
echo "  - https://tiaadeals.com"
echo "  - https://www.tiaadeals.com" 