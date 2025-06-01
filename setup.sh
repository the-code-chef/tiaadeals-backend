#!/bin/bash

# Exit on error
set -e

echo "Starting TiaaDeals Backend Setup..."

# Create necessary directories
echo "Creating directories..."
sudo mkdir -p /var/www/tiaadeals-backend
sudo mkdir -p /var/www/tiaadeals-backend/logs
sudo mkdir -p /var/run

# Set proper permissions
echo "Setting permissions..."
sudo chown -R ubuntu:www-data /var/www/tiaadeals-backend
sudo chmod -R 755 /var/www/tiaadeals-backend
sudo chmod -R 775 /var/www/tiaadeals-backend/logs

# Install Node.js if not installed
if ! command -v node &> /dev/null; then
    echo "Installing Node.js..."
    curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
    sudo apt-get install -y nodejs
fi

# Install PM2 globally if not installed
if ! command -v pm2 &> /dev/null; then
    echo "Installing PM2..."
    sudo npm install -g pm2
fi

# Install Nginx if not installed
if ! command -v nginx &> /dev/null; then
    echo "Installing Nginx..."
    sudo apt-get update
    sudo apt-get install -y nginx
fi

# Install Certbot for SSL
if ! command -v certbot &> /dev/null; then
    echo "Installing Certbot..."
    sudo apt-get install -y certbot python3-certbot-nginx
fi

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
    
    # Redirect all HTTP traffic to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name tiaadeals.com www.tiaadeals.com;

    # SSL configuration
    ssl_certificate /etc/letsencrypt/live/tiaadeals.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/tiaadeals.com/privkey.pem;
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

    # Logging
    access_log /var/log/nginx/tiaadeals.access.log;
    error_log /var/log/nginx/tiaadeals.error.log;

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

# Create systemd service file
echo "Creating systemd service..."
sudo tee /etc/systemd/system/tiaadeals.service << 'EOF'
[Unit]
Description=TiaaDeals Backend Service
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

# Create systemd socket file
echo "Creating systemd socket..."
sudo tee /etc/systemd/system/tiaadeals.socket << 'EOF'
[Unit]
Description=TiaaDeals Unix Socket
After=network.target

[Socket]
SocketPath=/var/run/tiaadeals.sock
SocketMode=0660
SocketUser=www-data
SocketGroup=ubuntu

[Install]
WantedBy=sockets.target
EOF

# Enable and start services
echo "Enabling and starting services..."
sudo ln -sf /etc/nginx/sites-available/tiaadeals /etc/nginx/sites-enabled/
sudo systemctl enable tiaadeals.socket
sudo systemctl enable tiaadeals
sudo systemctl enable nginx

# Get SSL certificate
echo "Getting SSL certificate..."
sudo certbot --nginx -d tiaadeals.com -d www.tiaadeals.com --non-interactive --agree-tos --email deepanshusinha94@gmail.com

# Restart services
echo "Restarting services..."
sudo systemctl restart nginx
sudo systemctl restart tiaadeals.socket
sudo systemctl restart tiaadeals

echo "Setup completed successfully!"
echo "Please check the logs for any errors:"
echo "sudo journalctl -u tiaadeals"
echo "sudo tail -f /var/www/tiaadeals-backend/logs/combined.log" 