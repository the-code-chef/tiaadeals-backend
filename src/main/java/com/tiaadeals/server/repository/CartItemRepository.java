package com.tiaadeals.server.repository;

import com.tiaadeals.server.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find cart items by user ID
     * 
     * @param userId the user ID to filter by
     * @return list of cart items for the user
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Find cart item by user ID and product ID
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return Optional containing the cart item if found
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Check if a cart item exists for user and product
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return true if cart item exists, false otherwise
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * Find cart items by user ID with product details
     * 
     * @param userId the user ID to filter by
     * @return list of cart items with product information
     */
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.user.id = :userId")
    List<CartItem> findByUserIdWithProduct(@Param("userId") Long userId);

    /**
     * Find cart items by user ID with product and category details
     * 
     * @param userId the user ID to filter by
     * @return list of cart items with product and category information
     */
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product p JOIN FETCH p.category WHERE ci.user.id = :userId")
    List<CartItem> findByUserIdWithProductAndCategory(@Param("userId") Long userId);

    /**
     * Find cart items with quantity greater than specified value
     * 
     * @param userId the user ID to filter by
     * @param quantity the minimum quantity
     * @return list of cart items with quantity greater than specified
     */
    List<CartItem> findByUserIdAndQuantityGreaterThan(Long userId, Integer quantity);

    /**
     * Find cart items with quantity less than or equal to specified value
     * 
     * @param userId the user ID to filter by
     * @param quantity the maximum quantity
     * @return list of cart items with quantity less than or equal to specified
     */
    List<CartItem> findByUserIdAndQuantityLessThanEqual(Long userId, Integer quantity);

    /**
     * Count total cart items for a user
     * 
     * @param userId the user ID to count cart items for
     * @return number of cart items for the user
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Calculate total cart value for a user
     * 
     * @param userId the user ID to calculate total for
     * @return total cart value
     */
    @Query("SELECT SUM(ci.quantity * p.price) FROM CartItem ci JOIN ci.product p WHERE ci.user.id = :userId")
    java.math.BigDecimal calculateTotalCartValue(@Param("userId") Long userId);

    /**
     * Find cart items with low stock products
     * 
     * @param userId the user ID to filter by
     * @param threshold the stock threshold
     * @return list of cart items with low stock products
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.product p WHERE ci.user.id = :userId AND p.stock <= :threshold")
    List<CartItem> findCartItemsWithLowStockProducts(@Param("userId") Long userId, @Param("threshold") Integer threshold);

    /**
     * Find cart items with out of stock products
     * 
     * @param userId the user ID to filter by
     * @return list of cart items with out of stock products
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.product p WHERE ci.user.id = :userId AND p.stock = 0")
    List<CartItem> findCartItemsWithOutOfStockProducts(@Param("userId") Long userId);

    /**
     * Find cart items by product ID
     * 
     * @param productId the product ID to filter by
     * @return list of cart items for the product
     */
    List<CartItem> findByProductId(Long productId);

    /**
     * Count how many users have a specific product in their cart
     * 
     * @param productId the product ID to count for
     * @return number of users with the product in their cart
     */
    @Query("SELECT COUNT(DISTINCT ci.user.id) FROM CartItem ci WHERE ci.product.id = :productId")
    long countUsersWithProductInCart(@Param("productId") Long productId);

    /**
     * Find cart items created after a specific date
     * 
     * @param userId the user ID to filter by
     * @param date the date to filter by
     * @return list of cart items created after the specified date
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId AND ci.createdAt >= :date")
    List<CartItem> findByUserIdAndCreatedAfter(@Param("userId") Long userId, @Param("date") java.time.LocalDateTime date);

    /**
     * Find cart items updated after a specific date
     * 
     * @param userId the user ID to filter by
     * @param date the date to filter by
     * @return list of cart items updated after the specified date
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId AND ci.updatedAt >= :date")
    List<CartItem> findByUserIdAndUpdatedAfter(@Param("userId") Long userId, @Param("date") java.time.LocalDateTime date);

    /**
     * Find cart items by category
     * 
     * @param userId the user ID to filter by
     * @param categoryId the category ID to filter by
     * @return list of cart items in the specified category
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.product p WHERE ci.user.id = :userId AND p.category.id = :categoryId")
    List<CartItem> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * Find cart items with high value (expensive products)
     * 
     * @param userId the user ID to filter by
     * @param minValue the minimum cart item value
     * @return list of cart items with high value
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.product p WHERE ci.user.id = :userId AND (ci.quantity * p.price) >= :minValue")
    List<CartItem> findCartItemsWithHighValue(@Param("userId") Long userId, @Param("minValue") java.math.BigDecimal minValue);

    /**
     * Find most popular products in carts
     * 
     * @param limit the number of products to return
     * @return list of most popular products in carts
     */
    @Query("SELECT ci.product, COUNT(ci) as cartCount FROM CartItem ci GROUP BY ci.product ORDER BY cartCount DESC")
    List<Object[]> findMostPopularProductsInCarts(@Param("limit") int limit);

    /**
     * Delete all cart items for a user
     * 
     * @param userId the user ID to delete cart items for
     */
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Delete cart items for a user and product
     * 
     * @param userId the user ID
     * @param productId the product ID
     */
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
} 