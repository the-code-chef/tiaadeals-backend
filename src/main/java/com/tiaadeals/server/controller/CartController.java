package com.tiaadeals.server.controller;

import com.tiaadeals.server.dto.CartItemDto;
import com.tiaadeals.server.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Cart management operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Shopping cart management APIs")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Get user's cart items
     * 
     * @param userId the user ID
     * @return list of cart items
     */
    @GetMapping("/{userId}")
    @Operation(
        summary = "Get user's cart",
        description = "Retrieves all items in a user's shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cart items retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<List<CartItemDto>> getUserCart(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        try {
            List<CartItemDto> cartItems = cartService.getUserCart(userId);
            return ResponseEntity.ok(cartItems);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add item to cart
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @param quantity the quantity to add
     * @return the added cart item
     */
    @PostMapping("/{userId}/add")
    @Operation(
        summary = "Add item to cart",
        description = "Adds a product to the user's shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Item added to cart successfully",
            content = @Content(schema = @Schema(implementation = CartItemDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or insufficient stock",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User or product not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<CartItemDto> addToCart(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Product ID", required = true, example = "1")
            @RequestParam Long productId,
            @Parameter(description = "Quantity to add", required = true, example = "1")
            @RequestParam Integer quantity) {
        
        try {
            CartItemDto cartItem = cartService.addToCart(userId, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update cart item quantity
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @param quantity the new quantity
     * @return the updated cart item
     */
    @PutMapping("/{userId}/update")
    @Operation(
        summary = "Update cart item quantity",
        description = "Updates the quantity of a specific item in the cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cart item updated successfully",
            content = @Content(schema = @Schema(implementation = CartItemDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid quantity or insufficient stock",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Cart item not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<CartItemDto> updateCartItemQuantity(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Product ID", required = true, example = "1")
            @RequestParam Long productId,
            @Parameter(description = "New quantity", required = true, example = "2")
            @RequestParam Integer quantity) {
        
        try {
            CartItemDto cartItem = cartService.updateCartItemQuantity(userId, productId, quantity);
            return ResponseEntity.ok(cartItem);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove item from cart
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return removal confirmation
     */
    @DeleteMapping("/{userId}/remove")
    @Operation(
        summary = "Remove item from cart",
        description = "Removes a specific item from the user's cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Item removed from cart successfully",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Cart item not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<String> removeFromCart(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Product ID", required = true, example = "1")
            @RequestParam Long productId) {
        
        try {
            cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok("Item removed from cart successfully");
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Clear user's cart
     * 
     * @param userId the user ID
     * @return clearing confirmation
     */
    @DeleteMapping("/{userId}/clear")
    @Operation(
        summary = "Clear user's cart",
        description = "Removes all items from the user's shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cart cleared successfully",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<String> clearCart(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("Cart cleared successfully");
            
        } catch (Exception e) {
            return ResponseEntity.ok("Cart cleared successfully");
        }
    }

    /**
     * Get cart total value
     * 
     * @param userId the user ID
     * @return the cart total value
     */
    @GetMapping("/{userId}/total")
    @Operation(
        summary = "Get cart total",
        description = "Calculates and returns the total value of items in the cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cart total calculated successfully",
            content = @Content(schema = @Schema(implementation = BigDecimal.class))
        )
    })
    public ResponseEntity<BigDecimal> getCartTotal(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        try {
            BigDecimal total = cartService.getCartTotal(userId);
            return ResponseEntity.ok(total);
            
        } catch (Exception e) {
            return ResponseEntity.ok(BigDecimal.ZERO);
        }
    }

    /**
     * Get cart item count
     * 
     * @param userId the user ID
     * @return the number of items in cart
     */
    @GetMapping("/{userId}/count")
    @Operation(
        summary = "Get cart item count",
        description = "Returns the total number of items in the user's cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cart count retrieved successfully",
            content = @Content(schema = @Schema(implementation = Long.class))
        )
    })
    public ResponseEntity<Long> getCartItemCount(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        try {
            long count = cartService.getCartItemCount(userId);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            return ResponseEntity.ok(0L);
        }
    }

    /**
     * Check if product is in user's cart
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return true if product is in cart, false otherwise
     */
    @GetMapping("/{userId}/contains")
    @Operation(
        summary = "Check if product is in cart",
        description = "Checks if a specific product is already in the user's cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Check completed successfully",
            content = @Content(schema = @Schema(implementation = Boolean.class))
        )
    })
    public ResponseEntity<Boolean> isProductInCart(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Product ID", required = true, example = "1")
            @RequestParam Long productId) {
        
        try {
            boolean isInCart = cartService.isProductInCart(userId, productId);
            return ResponseEntity.ok(isInCart);
            
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Get cart items with low stock products
     * 
     * @param userId the user ID
     * @param threshold the stock threshold
     * @return list of cart items with low stock products
     */
    @GetMapping("/{userId}/low-stock")
    @Operation(
        summary = "Get cart items with low stock",
        description = "Retrieves cart items where the product has low stock"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Low stock items retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<CartItemDto>> getCartItemsWithLowStock(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Stock threshold", required = true, example = "10")
            @RequestParam Integer threshold) {
        
        try {
            List<CartItemDto> cartItems = cartService.getCartItemsWithLowStock(userId, threshold);
            return ResponseEntity.ok(cartItems);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get cart items with out of stock products
     * 
     * @param userId the user ID
     * @return list of cart items with out of stock products
     */
    @GetMapping("/{userId}/out-of-stock")
    @Operation(
        summary = "Get cart items with out of stock products",
        description = "Retrieves cart items where the product is out of stock"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Out of stock items retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<CartItemDto>> getCartItemsWithOutOfStock(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        try {
            List<CartItemDto> cartItems = cartService.getCartItemsWithOutOfStock(userId);
            return ResponseEntity.ok(cartItems);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get cart items by category
     * 
     * @param userId the user ID
     * @param categoryId the category ID
     * @return list of cart items in the specified category
     */
    @GetMapping("/{userId}/category/{categoryId}")
    @Operation(
        summary = "Get cart items by category",
        description = "Retrieves cart items belonging to a specific category"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Category items retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<CartItemDto>> getCartItemsByCategory(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        
        try {
            List<CartItemDto> cartItems = cartService.getCartItemsByCategory(userId, categoryId);
            return ResponseEntity.ok(cartItems);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get cart items with high value
     * 
     * @param userId the user ID
     * @param minValue the minimum cart item value
     * @return list of cart items with high value
     */
    @GetMapping("/{userId}/high-value")
    @Operation(
        summary = "Get cart items with high value",
        description = "Retrieves cart items with a value above the specified threshold"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "High value items retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<CartItemDto>> getCartItemsWithHighValue(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Minimum value threshold", required = true, example = "100.00")
            @RequestParam BigDecimal minValue) {
        
        try {
            List<CartItemDto> cartItems = cartService.getCartItemsWithHighValue(userId, minValue);
            return ResponseEntity.ok(cartItems);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Validate cart items stock
     * 
     * @param userId the user ID
     * @return list of cart items with insufficient stock
     */
    @GetMapping("/{userId}/validate-stock")
    @Operation(
        summary = "Validate cart items stock",
        description = "Validates that all cart items have sufficient stock available"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Stock validation completed",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<CartItemDto>> validateCartStock(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        try {
            List<CartItemDto> invalidItems = cartService.validateCartStock(userId);
            return ResponseEntity.ok(invalidItems);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get most popular products in carts
     * 
     * @param limit the number of products to return
     * @return list of popular products with cart counts
     */
    @GetMapping("/popular-products")
    @Operation(
        summary = "Get most popular products in carts",
        description = "Retrieves the most popular products that are in users' carts"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Popular products retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<Object[]>> getMostPopularProductsInCarts(
            @Parameter(description = "Number of products to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Object[]> popularProducts = cartService.getMostPopularProductsInCarts(limit);
            return ResponseEntity.ok(popularProducts);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
} 