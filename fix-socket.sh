#!/bin/bash

# Stop services
echo "Stopping services..."
sudo systemctl stop tiaadeals.socket
sudo systemctl stop tiaadeals

# Remove old socket file if it exists
echo "Removing old socket file..."
sudo rm -f /var/run/tiaadeals.sock

# Create socket directory with proper permissions
echo "Setting up socket directory..."
sudo mkdir -p /var/run
sudo chown www-data:ubuntu /var/run
sudo chmod 775 /var/run

# Create new socket unit file
echo "Creating new socket unit file..."
sudo tee /etc/systemd/system/tiaadeals.socket << 'EOF'
[Unit]
Description=TiaaDeals Unix Socket
After=network.target

[Socket]
ListenStream=/var/run/tiaadeals.sock
SocketMode=0660
SocketUser=www-data
SocketGroup=ubuntu
DirectoryMode=0755

[Install]
WantedBy=sockets.target
EOF

# Reload systemd
echo "Reloading systemd..."
sudo systemctl daemon-reload

# Enable and start socket
echo "Enabling and starting socket..."
sudo systemctl enable tiaadeals.socket
sudo systemctl start tiaadeals.socket

# Start the service
echo "Starting the service..."
sudo systemctl start tiaadeals

# Check status
echo "Checking service status..."
sudo systemctl status tiaadeals.socket
sudo systemctl status tiaadeals

echo "Socket configuration completed!"
