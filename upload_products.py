#!/usr/bin/env python3
"""
TiaaDeals Product Upload Script
Uploads all products from products_data_remaining.json to the API
"""

import json
import requests
import time
from datetime import datetime

# Configuration
BASE_URL = "http://localhost:8080/api/v1/products"
JSON_FILE = "products_data_remaining.json"
LOG_FILE = "upload_results_remaining.txt"

def log_message(message):
    """Log message to both console and file"""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    log_entry = f"[{timestamp}] {message}"
    print(log_entry)
    with open(LOG_FILE, "a", encoding="utf-8") as f:
        f.write(log_entry + "\n")

def upload_product(product_data):
    """Upload a single product to the API"""
    try:
        response = requests.post(
            BASE_URL,
            json=product_data,
            headers={"Content-Type": "application/json"},
            timeout=30
        )
        
        if response.status_code == 201:
            return True, response.json()
        else:
            return False, f"HTTP {response.status_code}: {response.text}"
            
    except requests.exceptions.RequestException as e:
        return False, f"Request failed: {str(e)}"

def main():
    """Main function to upload all products"""
    log_message("=" * 60)
    log_message("Starting TiaaDeals Remaining Product Upload")
    log_message("=" * 60)
    
    # Load products from JSON file
    try:
        with open(JSON_FILE, 'r', encoding='utf-8') as f:
            products = json.load(f)
        log_message(f"Loaded {len(products)} products from {JSON_FILE}")
    except FileNotFoundError:
        log_message(f"❌ Error: {JSON_FILE} not found!")
        return
    except json.JSONDecodeError as e:
        log_message(f"❌ Error: Invalid JSON in {JSON_FILE}: {e}")
        return
    
    # Upload products
    successful = 0
    failed = 0
    
    for i, product in enumerate(products, 1):
        product_name = product.get('name', f'Product {i}')
        log_message(f"Uploading {i}/{len(products)}: {product_name}")
        
        success, result = upload_product(product)
        
        if success:
            log_message(f"✅ Success: {product_name}")
            successful += 1
        else:
            log_message(f"❌ Failed: {product_name} - {result}")
            failed += 1
        
        # Small delay to avoid overwhelming the server
        time.sleep(0.5)
    
    # Summary
    log_message("=" * 60)
    log_message("Upload Summary:")
    log_message(f"Total products: {len(products)}")
    log_message(f"Successful: {successful}")
    log_message(f"Failed: {failed}")
    log_message(f"Success rate: {(successful/len(products)*100):.1f}%")
    log_message("=" * 60)
    
    if failed > 0:
        log_message("⚠️  Some products failed to upload. Check the log above for details.")
    else:
        log_message("🎉 All products uploaded successfully!")

if __name__ == "__main__":
    main() 