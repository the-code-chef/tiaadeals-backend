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

# Create initial Nginx configuration (HTTP only)
echo "Creating initial Nginx configuration..."
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
    server_name api.tiaadeals.com;
    
    # Allow Let's Encrypt to verify domain ownership
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # API endpoints
    location /api/ {
        # Rate limiting
        limit_req zone=api_limit burst=20 nodelay;
        
        proxy_pass http://unix:/var/run/tiaadeals.sock;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $connection_upgrade;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffer size
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://unix:/var/run/tiaadeals.sock;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Deny access to hidden files
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }
}
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
sudo certbot --nginx -d api.tiaadeals.com --non-interactive --agree-tos --email deepanshusinha94@gmail.com

# Update Nginx configuration with SSL and CORS
echo "Updating Nginx configuration with SSL and CORS..."
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
    server_name api.tiaadeals.com;
    
    # Allow Let's Encrypt to verify domain ownership
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Redirect all HTTP traffic to HTTPS
    location / {
        return 301 https://$server_name$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name api.tiaadeals.com;

    # SSL configuration
    ssl_certificate /etc/letsencrypt/live/api.tiaadeals.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.tiaadeals.com/privkey.pem;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:50m;
    ssl_session_tickets off;

    # Modern configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
    add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;

    # CORS headers
    add_header 'Access-Control-Allow-Origin' $http_origin always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
    add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;
    add_header 'Access-Control-Allow-Credentials' 'true' always;

    # Logging
    access_log /var/log/nginx/tiaadeals.access.log;
    error_log /var/log/nginx/tiaadeals.error.log;

    # API endpoints
    location /api/ {
        # Rate limiting
        limit_req zone=api_limit burst=20 nodelay;
        
        # CORS preflight
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' $http_origin always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
            add_header 'Access-Control-Max-Age' 1728000;
            add_header 'Access-Control-Allow-Credentials' 'true' always;
            add_header 'Content-Type' 'text/plain; charset=utf-8';
            add_header 'Content-Length' 0;
            return 204;
        }
        
        proxy_pass http://unix:/var/run/tiaadeals.sock;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $connection_upgrade;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffer size
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://unix:/var/run/tiaadeals.sock;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Deny access to hidden files
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }
}
EOF

# Final restart of services
echo "Performing final restart of services..."
sudo systemctl restart nginx
sudo systemctl restart tiaadeals.socket
sudo systemctl restart tiaadeals

echo "Server setup completed!"
echo "Please update the .env.production file with your actual credentials."
echo "You can check the application status with: pm2 status"
echo "View logs with: pm2 logs tiaadeals-backend" 