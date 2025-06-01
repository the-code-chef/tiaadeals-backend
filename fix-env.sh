#!/bin/bash

# Stop the application
echo "Stopping the application..."
pm2 stop tiaadeals-backend

# Create .env.production file with proper content
echo "Creating .env.production file..."
sudo tee /var/www/tiaadeals-backend/.env.production << 'EOF'
# Server Configuration
NODE_ENV=production
PORT=3000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=tiaadeals
DB_USER=tiaadeals_user
DB_PASSWORD=your_secure_password_here

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_here
JWT_EXPIRES_IN=7d

# Logging
LOG_LEVEL=info
LOG_DIR=/var/www/tiaadeals-backend/logs
EOF

# Set proper permissions
echo "Setting permissions..."
sudo chown ubuntu:www-data /var/www/tiaadeals-backend/.env.production
sudo chmod 640 /var/www/tiaadeals-backend/.env.production

# Create logs directory
echo "Setting up logs directory..."
sudo mkdir -p /var/www/tiaadeals-backend/logs
sudo chown -R ubuntu:www-data /var/www/tiaadeals-backend/logs
sudo chmod -R 775 /var/www/tiaadeals-backend/logs

# Verify the database setup
echo "Verifying database setup..."
sudo -u postgres psql << 'EOF'
\c tiaadeals
\q
EOF

# If database doesn't exist, create it
if [ $? -ne 0 ]; then
    echo "Creating database and user..."
    sudo -u postgres psql << 'EOF'
CREATE DATABASE tiaadeals;
CREATE USER tiaadeals_user WITH ENCRYPTED PASSWORD 'your_secure_password_here';
GRANT ALL PRIVILEGES ON DATABASE tiaadeals TO tiaadeals_user;
\c tiaadeals
GRANT ALL ON SCHEMA public TO tiaadeals_user;
EOF
fi

# Update PM2 ecosystem config
echo "Updating PM2 configuration..."
cat > /var/www/tiaadeals-backend/ecosystem.config.js << 'EOF'
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
      NODE_ENV: 'production',
      ENV_FILE: '/var/www/tiaadeals-backend/.env.production'
    }
  }]
};
EOF

# Start the application
echo "Starting the application..."
cd /var/www/tiaadeals-backend
pm2 start ecosystem.config.js --env production

# Save PM2 configuration
pm2 save

echo "Environment setup completed!"
echo "Checking logs..."
pm2 logs tiaadeals-backend 