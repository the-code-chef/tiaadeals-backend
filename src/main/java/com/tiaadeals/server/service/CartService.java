package com.tiaadeals.server.service;

import com.tiaadeals.server.dto.CartItemDto;
import com.tiaadeals.server.entity.CartItem;
import com.tiaadeals.server.entity.Product;
import com.tiaadeals.server.entity.User;
import com.tiaadeals.server.exception.InsufficientStockException;
import com.tiaadeals.server.exception.ResourceNotFoundException;
import com.tiaadeals.server.repository.CartItemRepository;
import com.tiaadeals.server.repository.ProductRepository;
import com.tiaadeals.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Cart business logic
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Add item to cart
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @param quantity the quantity to add
     * @return the cart item DTO
     * @throws ResourceNotFoundException if user or product not found
     * @throws InsufficientStockException if insufficient stock
     */
    public CartItemDto addToCart(Long userId, Long productId, Integer quantity) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate product exists and is active
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Product is not available for purchase");
        }

        // Check stock availability
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);

        if (existingItem != null) {
            // Update existing item quantity
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setUpdatedAt(LocalDateTime.now());
            CartItem updatedItem = cartItemRepository.save(existingItem);
            return convertToDto(updatedItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());

            CartItem savedItem = cartItemRepository.save(cartItem);
            return convertToDto(savedItem);
        }
    }

    /**
     * Update cart item quantity
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @param quantity the new quantity
     * @return the updated cart item DTO
     * @throws ResourceNotFoundException if cart item not found
     * @throws InsufficientStockException if insufficient stock
     */
    public CartItemDto updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Check stock availability
        if (cartItem.getProduct().getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + cartItem.getProduct().getName());
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());

        CartItem updatedItem = cartItemRepository.save(cartItem);
        return convertToDto(updatedItem);
    }

    /**
     * Remove item from cart
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @throws ResourceNotFoundException if cart item not found
     */
    public void removeFromCart(Long userId, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItemRepository.delete(cartItem);
    }

    /**
     * Clear user's cart
     * 
     * @param userId the user ID
     */
    public void clearCart(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }

    /**
     * Get user's cart items
     * 
     * @param userId the user ID
     * @return list of cart item DTOs
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getUserCart(Long userId) {
        return cartItemRepository.findByUserIdWithProductAndCategory(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get cart item by ID
     * 
     * @param cartItemId the cart item ID
     * @return the cart item DTO
     * @throws ResourceNotFoundException if cart item not found
     */
    @Transactional(readOnly = true)
    public CartItemDto getCartItemById(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        return convertToDto(cartItem);
    }

    /**
     * Get cart total value
     * 
     * @param userId the user ID
     * @return the total cart value
     */
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        BigDecimal total = cartItemRepository.calculateTotalCartValue(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get cart item count
     * 
     * @param userId the user ID
     * @return the number of items in cart
     */
    @Transactional(readOnly = true)
    public long getCartItemCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }

    /**
     * Check if product is in user's cart
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return true if product is in cart, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isProductInCart(Long userId, Long productId) {
        return cartItemRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Get cart items with low stock products
     * 
     * @param userId the user ID
     * @param threshold the stock threshold
     * @return list of cart item DTOs with low stock products
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsWithLowStock(Long userId, Integer threshold) {
        return cartItemRepository.findCartItemsWithLowStockProducts(userId, threshold)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get cart items with out of stock products
     * 
     * @param userId the user ID
     * @return list of cart item DTOs with out of stock products
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsWithOutOfStock(Long userId) {
        return cartItemRepository.findCartItemsWithOutOfStockProducts(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get cart items by category
     * 
     * @param userId the user ID
     * @param categoryId the category ID
     * @return list of cart item DTOs in the specified category
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsByCategory(Long userId, Long categoryId) {
        return cartItemRepository.findByUserIdAndCategoryId(userId, categoryId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get cart items with high value
     * 
     * @param userId the user ID
     * @param minValue the minimum cart item value
     * @return list of cart item DTOs with high value
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsWithHighValue(Long userId, BigDecimal minValue) {
        return cartItemRepository.findCartItemsWithHighValue(userId, minValue)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Validate cart items stock
     * 
     * @param userId the user ID
     * @return list of cart item DTOs with insufficient stock
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> validateCartStock(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdWithProductAndCategory(userId);
        
        return cartItems.stream()
                .filter(item -> item.getProduct().getStock() < item.getQuantity())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get most popular products in carts
     * 
     * @param limit the number of products to return
     * @return list of popular products with cart counts
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMostPopularProductsInCarts(int limit) {
        return cartItemRepository.findMostPopularProductsInCarts(limit);
    }

    /**
     * Convert CartItem entity to CartItemDto
     * 
     * @param cartItem the cart item entity
     * @return the cart item DTO
     */
    private CartItemDto convertToDto(CartItem cartItem) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setUserId(cartItem.getUser().getId());
        cartItemDto.setProductId(cartItem.getProduct().getId());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setProductName(cartItem.getProduct().getName());
        cartItemDto.setProductPrice(cartItem.getProduct().getPrice());
        cartItemDto.setProductImageUrl(cartItem.getProduct().getImageUrl());
                    cartItemDto.setCategoryName(cartItem.getProduct().getCategory().getCategoryName());
        cartItemDto.setAvailableStock(cartItem.getProduct().getStock());
        cartItemDto.setIsInStock(cartItem.getProduct().getStock() > 0);
        cartItemDto.setCreatedAt(cartItem.getCreatedAt());
        cartItemDto.setUpdatedAt(cartItem.getUpdatedAt());
        
        // Calculate total price
        cartItemDto.calculateTotalPrice();
        
        return cartItemDto;
    }
} 