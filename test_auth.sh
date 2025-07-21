#!/bin/bash

# Test Authentication Endpoints

echo "=== Testing Authentication ==="

# Register a new user
echo "1. Registering a new user..."
REGISTER_RESPONSE=$(curl -s -X 'POST' 'http://localhost:8080/api/v1/auth/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Cart",
    "lastName": "Tester", 
    "email": "cart@test.com",
    "username": "carttester",
    "password": "TestPass123!"
  }')

echo "Register Response:"
echo "$REGISTER_RESPONSE" | jq '.'

# Extract token from response
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token')

if [ "$TOKEN" != "null" ] && [ "$TOKEN" != "" ]; then
    echo ""
    echo "2. Testing profile endpoint with token..."
    
    PROFILE_RESPONSE=$(curl -s -X 'GET' 'http://localhost:8080/api/v1/auth/profile' \
      -H 'accept: */*' \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Profile Response:"
    echo "$PROFILE_RESPONSE" | jq '.'
    
    echo ""
    echo "3. Testing cart endpoint with token..."
    
    CART_RESPONSE=$(curl -s -X 'GET' 'http://localhost:8080/api/v1/cart/2' \
      -H 'accept: */*' \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Cart Response:"
    echo "$CART_RESPONSE" | jq '.'
    
else
    echo "Failed to get token from registration response"
fi 