#!/bin/bash

# TiaaDeals Product Upload Script
# This script uploads all products from the JSON data to the API

BASE_URL="http://localhost:8080/api/v1/products"
LOG_FILE="upload_log.txt"

echo "Starting product upload at $(date)" | tee $LOG_FILE
echo "========================================" | tee -a $LOG_FILE

# Function to upload a single product
upload_product() {
    local product_data="$1"
    local product_name=$(echo "$product_data" | jq -r '.name')
    
    echo "Uploading: $product_name" | tee -a $LOG_FILE
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
        -H "Content-Type: application/json" \
        -d "$product_data")
    
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 201 ]; then
        echo "✅ Success: $product_name" | tee -a $LOG_FILE
    else
        echo "❌ Failed: $product_name (HTTP $http_code)" | tee -a $LOG_FILE
        echo "Response: $response_body" | tee -a $LOG_FILE
    fi
    
    echo "---" | tee -a $LOG_FILE
}

# Product 1: mi book 15
upload_product '{
  "name": "mi book 15",
  "price": 31990,
  "originalPrice": 51999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908106/redmi-book-15_ksizgp.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 10},
    {"color": "#00ff00", "colorQuantity": 6},
    {"color": "#ff0000", "colorQuantity": 9}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 39.62 cm and hard disk size is 256 GB. CPU Model Core is i3. RAM Memory Installed Size is 8 GB. Operating System is Windows 10 Home. Special Feature includes Anti Glare Screen, Light Weight, Thin. Graphics Card is Integrated",
  "category": "laptop",
  "isShippingAvailable": true,
  "stock": 25,
  "reviewCount": 418,
  "stars": 3.7
}'

# Product 2: mi notebook pro
upload_product '{
  "name": "mi notebook pro",
  "price": 54499,
  "originalPrice": 74999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908295/mi-notebook-pro_hi4vih.jpg",
  "colors": [
    {"color": "#00ff00", "colorQuantity": 8},
    {"color": "#000", "colorQuantity": 2}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 14 Inches and hard disk size is 512 GB. CPU Model Core is i5. RAM Memory Installed Size is 16 GB. Operating System is Windows 11. Special Feature includes Fingerprint Reader, Backlit Keyboard. Graphics Card is Integrated",
  "category": "laptop",
  "isShippingAvailable": false,
  "stock": 10,
  "reviewCount": 1805,
  "stars": 4.3
}'

# Product 3: mi 5A
upload_product '{
  "name": "mi 5A",
  "price": 13499,
  "originalPrice": 24999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908781/mi-5a_spazxt.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 9},
    {"color": "#00ff00", "colorQuantity": 2},
    {"color": "#ff0000", "colorQuantity": 7},
    {"color": "#ffb900", "colorQuantity": 9}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 32 Inches. Product Dimensions is (19D x 71.5W x 47H) cm. Operating System is Windows 11. Mounting Hardware includes 1 LED TV, 2 Table Stand Base, 1 User Manual, 1 Remote Control, 4 screws, 2 x AAA Batteries. Resolution is 720p with the refresh rate is 60Hz. Tapu streams NETFLIX using this device 🧡",
  "category": "tv",
  "isShippingAvailable": true,
  "stock": 27,
  "reviewCount": 35573,
  "stars": 4.2
}'

echo "Upload completed at $(date)" | tee -a $LOG_FILE
echo "Check $LOG_FILE for detailed results" 