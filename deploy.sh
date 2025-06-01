#!/bin/bash

# Exit on error
set -e

echo "Starting TiaaDeals Backend Deployment..."

# Update system packages
echo "Updating system packages..."
sudo apt-get update
sudo apt-get upgrade -y

# Install Node.js
echo "Installing Node.js..."
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install PM2 globally
echo "Installing PM2..."
sudo npm install -g pm2

# Install PostgreSQL
echo "Installing PostgreSQL..."
sudo apt-get install -y postgresql postgresql-contrib

# Install Nginx
echo "Installing Nginx..."
sudo apt-get install -y nginx

# Install Certbot for SSL
echo "Installing Certbot..."
sudo apt-get install -y certbot python3-certbot-nginx

# Create application directory
echo "Creating application directory..."
sudo mkdir -p /var/www/tiaadeals-backend
sudo chown -R ubuntu:www-data /var/www/tiaadeals-backend

# Create logs directory
echo "Creating logs directory..."
sudo mkdir -p /var/www/tiaadeals-backend/logs
sudo chown -R ubuntu:www-data /var/www/tiaadeals-backend/logs
sudo chmod -R 775 /var/www/tiaadeals-backend/logs

# Create .env.production file
echo "Creating production environment file..."
sudo tee /var/www/tiaadeals-backend/.env.production << 'EOF'
# Server Configuration
NODE_ENV=production
PORT=3000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=tiaadeals
DB_USER=tiaadeals_user
DB_PASSWORD=Ti@@De@ls

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_here
JWT_EXPIRES_IN=7d

# Logging
LOG_LEVEL=info
LOG_DIR=/var/www/tiaadeals-backend/logs
EOF

# Set proper permissions
echo "Setting file permissions..."
sudo chown ubuntu:www-data /var/www/tiaadeals-backend/.env.production
sudo chmod 640 /var/www/tiaadeals-backend/.env.production

# Setup PostgreSQL
echo "Setting up PostgreSQL..."
sudo -u postgres psql << 'EOF'
CREATE DATABASE tiaadeals;
CREATE USER tiaadeals_user WITH ENCRYPTED PASSWORD 'Ti@@De@ls';
GRANT ALL PRIVILEGES ON DATABASE tiaadeals TO tiaadeals_user;
\c tiaadeals
GRANT ALL ON SCHEMA public TO tiaadeals_user;
EOF

# Create PM2 ecosystem config
echo "Creating PM2 configuration..."
sudo tee /var/www/tiaadeals-backend/ecosystem.config.js << 'EOF'
module.exports = {
  apps: [{
    name: 'tiaadeals-backend',
    script: '/var/www/tiaadeals-backend/src/server.js',
    instances: 1,
    autorestart: true,
    watch: false,
    max_memory_restart: '300M',
    env: {
      NODE_ENV: 'production'
    },
    env_production: {
      NODE_ENV: 'production'
    },
    error_file: '/var/www/tiaadeals-backend/logs/err.log',
    out_file: '/var/www/tiaadeals-backend/logs/out.log',
    log_file: '/var/www/tiaadeals-backend/logs/combined.log',
    time: true
  }]
};
EOF

# Create systemd socket file
echo "Creating systemd socket..."
sudo tee /etc/systemd/system/tiaadeals.socket << 'EOF'
[Unit]
Description=TiaaDeals Unix Socket
PartOf=tiaadeals.service

[Socket]
ListenStream=/var/run/tiaadeals.sock
SocketMode=0660
SocketUser=www-data
SocketGroup=ubuntu

[Install]
WantedBy=sockets.target
EOF

# Create systemd service file
echo "Creating systemd service..."
sudo tee /etc/systemd/system/tiaadeals.service << 'EOF'
[Unit]
Description=TiaaDeals Backend Service
Requires=tiaadeals.socket
After=network.target

[Service]
Type=simple
User=ubuntu
Group=ubuntu
WorkingDirectory=/var/www/tiaadeals-backend
ExecStart=/usr/bin/pm2 start ecosystem.config.js
ExecReload=/usr/bin/pm2 reload ecosystem.config.js
ExecStop=/usr/bin/pm2 stop ecosystem.config.js
Restart=on-failure
Environment=NODE_ENV=production

[Install]
WantedBy=multi-user.target
EOF

# Create Nginx configuration
echo "Creating Nginx configuration..."
sudo tee /etc/nginx/sites-available/tiaadeals << 'EOF'
# Security headers
map $http_upgrade $connection_upgrade {
    default upgrade;
    '' close;
}

# Rate limiting
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

server {
    listen 80;
    server_name tiaadeals.com www.tiaadeals.com;
    
    # Allow Let's Encrypt to verify domain ownership
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Redirect all other HTTP traffic to HTTPS
    location / {
        return 301 https://$server_name$request_uri;
    }
}

# HTTPS server will be added by certbot
EOF

# Enable and start services
echo "Enabling and starting services..."
sudo ln -sf /etc/nginx/sites-available/tiaadeals /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo systemctl daemon-reload
sudo systemctl enable tiaadeals.socket
sudo systemctl enable tiaadeals
sudo systemctl enable nginx

# Start services
echo "Starting services..."
sudo systemctl restart nginx
sudo systemctl restart tiaadeals.socket
sudo systemctl restart tiaadeals

# Get SSL certificate
echo "Getting SSL certificate..."
sudo certbot --nginx -d tiaadeals.com -d www.tiaadeals.com --non-interactive --agree-tos --email deepanshusinha94@gmail.com

# Final restart of services
echo "Performing final restart of services..."
sudo systemctl restart nginx
sudo systemctl restart tiaadeals.socket
sudo systemctl restart tiaadeals

echo "Server setup completed!"
echo "Please update the .env.production file with your actual credentials."
echo "You can check the application status with: pm2 status"
echo "View logs with: pm2 logs tiaadeals-backend" 