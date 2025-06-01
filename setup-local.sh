#!/bin/bash

# Create .env file for local development
echo "Creating .env file for local development..."
cat > .env << 'EOF'
# Server Configuration
NODE_ENV=development
PORT=3000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=tiaadeals
DB_USER=tiaadeals_user
DB_PASSWORD=your_secure_password_here

# JWT Configuration
JWT_SECRET=your_development_jwt_secret_here
JWT_EXPIRES_IN=7d

# Logging
LOG_LEVEL=debug
LOG_DIR=logs
EOF

# Create logs directory
echo "Creating logs directory..."
mkdir -p logs

# Install dependencies
echo "Installing dependencies..."
npm install

echo "Local development environment setup completed!"
echo "Please update the .env file with your actual database credentials and JWT secret."
echo "You can now start the server with: npm start" 