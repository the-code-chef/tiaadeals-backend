#!/bin/bash

# TiaaDeals Complete Product Upload Script
# This script uploads all products from the JSON data to the API

BASE_URL="http://localhost:8080/api/v1/products"
LOG_FILE="complete_upload_log.txt"

echo "Starting complete product upload at $(date)" | tee $LOG_FILE
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
    sleep 0.5  # Small delay to avoid overwhelming the server
}

# Batch 1: More Redmi Products
echo "Uploading Batch 1: Redmi Products" | tee -a $LOG_FILE

upload_product '{
  "name": "mi horizon",
  "price": 21399,
  "originalPrice": 29999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909215/mi-horizon_n4anpx.jpg",
  "colors": [{"color": "#000", "colorQuantity": 4}],
  "company": "redmi",
  "description": "For this model, screen size is 40 Inches. Product Dimensions is (8.7D x 89.2W x 55.9H) cm. Operating System is Windows 11. Mounting Hardware includes 1 LED TV, 2 Table Stand Base, 1 User Manual, 1 Remote Control, 4 screws. Resolution is 1080p with the refresh rate is 60Hz.",
  "category": "tv",
  "isShippingAvailable": false,
  "stock": 4,
  "reviewCount": 35573,
  "stars": 4.1
}'

upload_product '{
  "name": "mi sonicBass",
  "price": 1299,
  "originalPrice": 1599,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909289/mi-sonicBass_zgkhiw.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 5},
    {"color": "#000", "colorQuantity": 7},
    {"color": "#ffb900", "colorQuantity": 3}
  ],
  "company": "redmi",
  "description": "This is a \"In Ear\" product and wireless and equipped with 9.2 mm dynamic drivers. Features include Dual-Mic Noise Cancellation, Dual Pairing Multi-Point Connection with Flexi Arc and Skin-friendly Design. This product is Atamaram Bhide\'s 🧡 and is in under common man budget.",
  "category": "earphone",
  "isShippingAvailable": true,
  "stock": 15,
  "reviewCount": 8238,
  "stars": 2.5
}'

upload_product '{
  "name": "mi buds 3 lite",
  "price": 2999,
  "originalPrice": 1599,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909559/mi-buds-3-lite_g4ntj3.jpg",
  "colors": [
    {"color": "#ff0000", "colorQuantity": 0},
    {"color": "#ffb900", "colorQuantity": 0}
  ],
  "company": "redmi",
  "description": "This is a \"In Ear\" product and wireless. Features include Lightweight with Lock-in Design, Bluetooth 5.2 and Type-C Compatibility.",
  "category": "earphone",
  "isShippingAvailable": false,
  "stock": 0,
  "reviewCount": 1101,
  "stars": 1.5
}'

upload_product '{
  "name": "mi smartband pro",
  "price": 1999,
  "originalPrice": 5999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909682/mi-smartband-pro_bzxg3v.jpg",
  "colors": [
    {"color": "#ff0000", "colorQuantity": 7},
    {"color": "#000", "colorQuantity": 2}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 3.73 cm and rectangular in shape. Special Feature includes Sleep Monitor, Sedentary Reminder, Activity Tracker, Alarm Clock, Calorie Tracker.",
  "category": "smartwatch",
  "isShippingAvailable": false,
  "stock": 9,
  "reviewCount": 20,
  "stars": 4.5
}'

upload_product '{
  "name": "mi go phone",
  "price": 5399,
  "originalPrice": 6499,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909795/mi-go-phone_obpz7q.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 2},
    {"color": "#00ff00", "colorQuantity": 5},
    {"color": "#ff0000", "colorQuantity": 6},
    {"color": "#000", "colorQuantity": 1}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 5 inches and RAM Memory is 1 GB. Operating System is Android 7.1. This is a 4G device",
  "category": "mobile",
  "isShippingAvailable": false,
  "stock": 14,
  "reviewCount": 70,
  "stars": 2.9
}'

upload_product '{
  "name": "mi 10 prime",
  "price": 14999,
  "originalPrice": 16999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909893/mi-10-prime_sspsnb.jpg",
  "colors": [
    {"color": "#ff0000", "colorQuantity": 10},
    {"color": "#000", "colorQuantity": 6},
    {"color": "#ffb900", "colorQuantity": 6}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 6.5 inches and RAM Memory is 6 GB. Cellular technology is LTE.",
  "category": "mobile",
  "isShippingAvailable": true,
  "stock": 22,
  "reviewCount": 607,
  "stars": 4.9
}'

upload_product '{
  "name": "mi note 12",
  "price": 17999,
  "originalPrice": 19999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910028/mi-note-12_w0gqbt.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 2},
    {"color": "#00ff00", "colorQuantity": 6}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 6.67 inches and RAM Memory is 128 GB. Operating Sysytem is MIUI 13. Cellular technology is 5G.",
  "category": "mobile",
  "isShippingAvailable": false,
  "stock": 8,
  "reviewCount": 3851,
  "stars": 2.9
}'

upload_product '{
  "name": "mi 9 activ",
  "price": 9499,
  "originalPrice": 10999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910103/mi-9-activ_kklapc.jpg",
  "colors": [
    {"color": "#00ff00", "colorQuantity": 10},
    {"color": "#000", "colorQuantity": 3}
  ],
  "company": "redmi",
  "description": "For this model, screen size is 6.53 inches and RAM Memory is 4 GB. Operating System is MIUI 20. Cellular technology is LTE.",
  "category": "mobile",
  "isShippingAvailable": true,
  "stock": 13,
  "reviewCount": 70,
  "stars": 1.3
}'

echo "Batch 1 completed. Starting Batch 2..." | tee -a $LOG_FILE

# Batch 2: Apple Products
echo "Uploading Batch 2: Apple Products" | tee -a $LOG_FILE

upload_product '{
  "name": "apple air 2020",
  "price": 82900,
  "originalPrice": 99900,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910169/apple-air-2020_kacdj5.jpg",
  "colors": [
    {"color": "#00ff00", "colorQuantity": 1},
    {"color": "#ffb900", "colorQuantity": 2}
  ],
  "company": "apple",
  "description": "For this model, screen size is 13 inches and hard disk size is 256 GB. CPU Model Core is Core M Family. RAM Memory Installed Size is 8 GB. Operating System is MacOS 10.14 Mojave. Special Feature is Backlit Keyboard. Graphics Card is Integrated",
  "category": "laptop",
  "isShippingAvailable": true,
  "stock": 3,
  "reviewCount": 456,
  "stars": 4.6
}'

upload_product '{
  "name": "apple air 2022",
  "price": 106999,
  "originalPrice": 119999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910465/apple-air-2022_ijki4z.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 9},
    {"color": "#ff0000", "colorQuantity": 8},
    {"color": "#000", "colorQuantity": 8},
    {"color": "#ffb900", "colorQuantity": 6}
  ],
  "company": "apple",
  "description": "For this model, screen size is 13.6 inches and hard disk size is 256 GB. RAM Memory Installed Size is 8 GB. Operating System is MacOS. Special Feature is Portable, Backlit Keyboard, Thin. Graphics Card is Integrated. This is the laptop Iyer bought after coffee got spilt on the previous laptop.",
  "category": "laptop",
  "isShippingAvailable": false,
  "stock": 31,
  "reviewCount": 7890,
  "stars": 3.3
}'

upload_product '{
  "name": "apple air 2023",
  "price": 110899,
  "originalPrice": 120999,
  "featured": true,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910482/apple-air-2023_b59fdw.jpg",
  "colors": [{"color": "#00ff00", "colorQuantity": 4}],
  "company": "apple",
  "description": "For this model, screen size is 14 inches and hard disk size is 1000 GB. CPU Model Core is Core M Family. RAM Memory Installed Size is 16 GB. Operating System is MacOS. Special Feature is Backlit Keyboard. Graphics Card is Integrated",
  "category": "laptop",
  "isShippingAvailable": true,
  "stock": 4,
  "reviewCount": 31110,
  "stars": 4.5
}'

upload_product '{
  "name": "apple prod display xdr",
  "price": 59899,
  "originalPrice": 89999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910548/apple-pro-display-xdr_gvs2i9.jpg",
  "colors": [
    {"color": "#00ff00", "colorQuantity": 7},
    {"color": "#ff0000", "colorQuantity": 8}
  ],
  "company": "apple",
  "description": "For this model, screen size is 32 Inches. Extreme Dynamic Range (XDR)",
  "category": "tv",
  "isShippingAvailable": false,
  "stock": 15,
  "reviewCount": 9,
  "stars": 2.6
}'

upload_product '{
  "name": "apple airPods pro",
  "price": 23625,
  "originalPrice": 24900,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910675/apple-airPods-pro_bmdiyx.jpg",
  "colors": [{"color": "#000", "colorQuantity": 6}],
  "company": "apple",
  "description": "This is a \"In Ear\" product and wireless. Features include Active Noise Cancellation, Spatial audio with dynamic head tracking and Adaptive EQ.",
  "category": "earphone",
  "isShippingAvailable": true,
  "stock": 6,
  "reviewCount": 16711,
  "stars": 4.5
}'

upload_product '{
  "name": "apple airPods max",
  "price": 25999,
  "originalPrice": 29999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683910968/apple-airPods-max_pbb9it.jpg",
  "colors": [
    {"color": "#ff0000", "colorQuantity": 5},
    {"color": "#000", "colorQuantity": 5}
  ],
  "company": "apple",
  "description": "This is a \"On Ear\" product. Features include Active Noise Cancellation, Spatial audio with dynamic head tracking and Adaptive EQ. Material used is Memory Foam",
  "category": "earphone",
  "isShippingAvailable": false,
  "stock": 10,
  "reviewCount": 420,
  "stars": 3.8
}'

upload_product '{
  "name": "apple watch ultra",
  "price": 56304,
  "originalPrice": 59900,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683911006/apple-watch-ultra_ony1kc.jpg",
  "colors": [
    {"color": "#00ff00", "colorQuantity": 6},
    {"color": "#000", "colorQuantity": 4},
    {"color": "#ffb900", "colorQuantity": 3}
  ],
  "company": "apple",
  "description": "For this model, screen size is 40 mm and rectangular in shape. Special Feature includes Retina Display, Heart Rate Notifications, Calls, Fall detection, etc. Babita Ji loves quality, so she loves Apple watch Ultra 🧡",
  "category": "smartwatch",
  "isShippingAvailable": true,
  "stock": 13,
  "reviewCount": 5454,
  "stars": 4.4
}'

upload_product '{
  "name": "apple watch SE",
  "price": 42999,
  "originalPrice": 45999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683911156/apple-watch-SE_uf7kjm.jpg",
  "colors": [
    {"color": "#ff0000", "colorQuantity": 0},
    {"color": "#000", "colorQuantity": 0},
    {"color": "#ffb900", "colorQuantity": 0}
  ],
  "company": "apple",
  "description": "For this model, screen size is 44 mm and rectangular in shape. Special Feature includes EASILY CUSTOMISABLE, HEALTH AND SAFETY FEATURES, SIMPLY COMPATIBLE,and SWIMPROOF AND STYLISH.",
  "category": "smartwatch",
  "isShippingAvailable": false,
  "stock": 0,
  "reviewCount": 420,
  "stars": 3.8
}'

upload_product '{
  "name": "iPhone 12",
  "price": 69000,
  "originalPrice": 74000,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683911348/iPhone-12_agdvwp.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 10},
    {"color": "#ff0000", "colorQuantity": 4},
    {"color": "#000", "colorQuantity": 2}
  ],
  "company": "apple",
  "description": "For this model, screen size is 6.1 inches and RAM Memory is 64 GB. Operating System is IOS 14. This is a 5G device",
  "category": "mobile",
  "isShippingAvailable": true,
  "stock": 16,
  "reviewCount": 5002,
  "stars": 3.2
}'

upload_product '{
  "name": "iPhone 14 plus",
  "price": 57999,
  "originalPrice": 58999,
  "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683911421/iPhone-14-plus_d4nmst.jpg",
  "colors": [
    {"color": "#0000ff", "colorQuantity": 9},
    {"color": "#00ff00", "colorQuantity": 2},
    {"color": "#ff0000", "colorQuantity": 10},
    {"color": "#000", "colorQuantity": 8},
    {"color": "#ffb900", "colorQuantity": 1}
  ],
  "company": "apple",
  "description": "For this model, screen size is 16.95cm and RAM Memory is 128 GB. Operating System is IOS. Special Feature is Cinematic mode in 4K Dolby Vision up to 30 fps. This is a 5G device",
  "category": "mobile",
  "isShippingAvailable": false,
  "stock": 30,
  "reviewCount": 22031,
  "stars": 4.7
}'

echo "Batch 2 completed. Starting Batch 3..." | tee -a $LOG_FILE

echo "Upload completed at $(date)" | tee -a $LOG_FILE
echo "Check $LOG_FILE for detailed results" 