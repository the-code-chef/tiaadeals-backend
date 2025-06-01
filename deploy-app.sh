#!/bin/bash

# Exit on error
set -e

echo "Starting application deployment..."

# Navigate to application directory
cd /var/www/tiaadeals-backend

# Install dependencies
echo "Installing dependencies..."
npm install

# Build application
echo "Building application..."
npm run build

# Copy application files
echo "Copying application files..."
sudo cp -r * /var/www/tiaadeals-backend/

# Set proper permissions
echo "Setting permissions..."
sudo chown -R ubuntu:www-data /var/www/tiaadeals-backend
sudo chmod -R 755 /var/www/tiaadeals-backend

# Restart services
echo "Restarting services..."
sudo systemctl restart tiaadeals.socket
sudo systemctl restart tiaadeals
sudo systemctl restart nginx

echo "Application deployment completed!"
echo "You can check the application status with: pm2 status"
echo "View logs with: pm2 logs tiaadeals-backend" 