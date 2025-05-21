#!/bin/bash

# Update system packages
sudo apt-get update
sudo apt-get upgrade -y

# Install Node.js and npm
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install PM2 globally
sudo npm install -g pm2

# Install nginx
sudo apt-get install -y nginx

# Install certbot for SSL
sudo apt-get install -y certbot python3-certbot-nginx

# Create nginx configuration
sudo tee /etc/nginx/sites-available/tiaadeals-backend << EOF
server {
    listen 80;
    server_name api.tiaadeals.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
    }
}
EOF

# Enable the site
sudo ln -s /etc/nginx/sites-available/tiaadeals-backend /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Test nginx configuration
sudo nginx -t

# Restart nginx
sudo systemctl restart nginx

# Setup SSL with Let's Encrypt
sudo certbot --nginx -d api.tiaadeals.com --non-interactive --agree-tos --email your-email@example.com

# Create .env file
cat > .env << EOF
NODE_ENV=production
PORT=3000
FRONTEND_URL=https://api.tiaadeals.com
DB_HOST=${DB_HOST}
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}
DB_NAME=${DB_NAME}
JWT_SECRET=${JWT_SECRET}
EOF

# Install dependencies
npm install

# Start the application with PM2
pm2 start ecosystem.config.js
pm2 save
pm2 startup 