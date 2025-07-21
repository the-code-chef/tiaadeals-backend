package com.tiaadeals.server.repository;

import com.tiaadeals.server.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WishlistItem entity operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    /**
     * Find wishlist items by user ID
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items for the user
     */
    List<WishlistItem> findByUserId(Long userId);

    /**
     * Find wishlist item by user ID and product ID
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return Optional containing the wishlist item if found
     */
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Check if a wishlist item exists for user and product
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return true if wishlist item exists, false otherwise
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * Find wishlist items by user ID with product details
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items with product information
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN FETCH wi.product WHERE wi.user.id = :userId")
    List<WishlistItem> findByUserIdWithProduct(@Param("userId") Long userId);

    /**
     * Find wishlist items by user ID with product and category details
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items with product and category information
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN FETCH wi.product p JOIN FETCH p.category WHERE wi.user.id = :userId")
    List<WishlistItem> findByUserIdWithProductAndCategory(@Param("userId") Long userId);

    /**
     * Count total wishlist items for a user
     * 
     * @param userId the user ID to count wishlist items for
     * @return number of wishlist items for the user
     */
    @Query("SELECT COUNT(wi) FROM WishlistItem wi WHERE wi.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Find wishlist items by product ID
     * 
     * @param productId the product ID to filter by
     * @return list of wishlist items for the product
     */
    List<WishlistItem> findByProductId(Long productId);

    /**
     * Count how many users have a specific product in their wishlist
     * 
     * @param productId the product ID to count for
     * @return number of users with the product in their wishlist
     */
    @Query("SELECT COUNT(DISTINCT wi.user.id) FROM WishlistItem wi WHERE wi.product.id = :productId")
    long countUsersWithProductInWishlist(@Param("productId") Long productId);

    /**
     * Find wishlist items created after a specific date
     * 
     * @param userId the user ID to filter by
     * @param date the date to filter by
     * @return list of wishlist items created after the specified date
     */
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.user.id = :userId AND wi.createdAt >= :date")
    List<WishlistItem> findByUserIdAndCreatedAfter(@Param("userId") Long userId, @Param("date") java.time.LocalDateTime date);

    /**
     * Find wishlist items by category
     * 
     * @param userId the user ID to filter by
     * @param categoryId the category ID to filter by
     * @return list of wishlist items in the specified category
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.category.id = :categoryId")
    List<WishlistItem> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * Find wishlist items with expensive products
     * 
     * @param userId the user ID to filter by
     * @param minPrice the minimum product price
     * @return list of wishlist items with expensive products
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.price >= :minPrice")
    List<WishlistItem> findWishlistItemsWithExpensiveProducts(@Param("userId") Long userId, @Param("minPrice") java.math.BigDecimal minPrice);

    /**
     * Find wishlist items with cheap products
     * 
     * @param userId the user ID to filter by
     * @param maxPrice the maximum product price
     * @return list of wishlist items with cheap products
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.price <= :maxPrice")
    List<WishlistItem> findWishlistItemsWithCheapProducts(@Param("userId") Long userId, @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Find wishlist items with out of stock products
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items with out of stock products
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.stock = 0")
    List<WishlistItem> findWishlistItemsWithOutOfStockProducts(@Param("userId") Long userId);

    /**
     * Find wishlist items with in-stock products
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items with in-stock products
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.stock > 0")
    List<WishlistItem> findWishlistItemsWithInStockProducts(@Param("userId") Long userId);

    /**
     * Find most popular products in wishlists
     * 
     * @param limit the number of products to return
     * @return list of most popular products in wishlists
     */
    @Query("SELECT wi.product, COUNT(wi) as wishlistCount FROM WishlistItem wi GROUP BY wi.product ORDER BY wishlistCount DESC")
    List<Object[]> findMostPopularProductsInWishlists(@Param("limit") int limit);

    /**
     * Find wishlist items by price range
     * 
     * @param userId the user ID to filter by
     * @param minPrice minimum product price
     * @param maxPrice maximum product price
     * @return list of wishlist items within the price range
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.price BETWEEN :minPrice AND :maxPrice")
    List<WishlistItem> findByUserIdAndPriceRange(
            @Param("userId") Long userId,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Find wishlist items by product name pattern
     * 
     * @param userId the user ID to filter by
     * @param namePattern the product name pattern to search for
     * @return list of wishlist items with matching product names
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND LOWER(p.name) LIKE LOWER(%:namePattern%)")
    List<WishlistItem> findByUserIdAndProductNamePattern(@Param("userId") Long userId, @Param("namePattern") String namePattern);

    /**
     * Find wishlist items by product description pattern
     * 
     * @param userId the user ID to filter by
     * @param descriptionPattern the product description pattern to search for
     * @return list of wishlist items with matching product descriptions
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND LOWER(p.description) LIKE LOWER(%:descriptionPattern%)")
    List<WishlistItem> findByUserIdAndProductDescriptionPattern(@Param("userId") Long userId, @Param("descriptionPattern") String descriptionPattern);

    /**
     * Find wishlist items by search term (product name or description)
     * 
     * @param userId the user ID to filter by
     * @param searchTerm the search term to look for in product name or description
     * @return list of wishlist items with matching search term
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND (LOWER(p.name) LIKE LOWER(%:searchTerm%) OR LOWER(p.description) LIKE LOWER(%:searchTerm%))")
    List<WishlistItem> findByUserIdAndSearchTerm(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    /**
     * Find recently added wishlist items
     * 
     * @param userId the user ID to filter by
     * @param limit the number of items to return
     * @return list of recently added wishlist items
     */
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.user.id = :userId ORDER BY wi.createdAt DESC LIMIT :limit")
    List<WishlistItem> findRecentlyAddedWishlistItems(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * Find wishlist items that are also in the user's cart
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items that are also in the cart
     */
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.user.id = :userId AND EXISTS (SELECT 1 FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = wi.product.id)")
    List<WishlistItem> findWishlistItemsAlsoInCart(@Param("userId") Long userId);

    /**
     * Find wishlist items that are not in the user's cart
     * 
     * @param userId the user ID to filter by
     * @return list of wishlist items that are not in the cart
     */
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.user.id = :userId AND NOT EXISTS (SELECT 1 FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = wi.product.id)")
    List<WishlistItem> findWishlistItemsNotInCart(@Param("userId") Long userId);

    /**
     * Delete all wishlist items for a user
     * 
     * @param userId the user ID to delete wishlist items for
     */
    @Query("DELETE FROM WishlistItem wi WHERE wi.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Delete wishlist items for a user and product
     * 
     * @param userId the user ID
     * @param productId the product ID
     */
    @Query("DELETE FROM WishlistItem wi WHERE wi.user.id = :userId AND wi.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * Find wishlist items by multiple categories
     * 
     * @param userId the user ID to filter by
     * @param categoryIds list of category IDs to filter by
     * @return list of wishlist items in the specified categories
     */
    @Query("SELECT wi FROM WishlistItem wi JOIN wi.product p WHERE wi.user.id = :userId AND p.category.id IN :categoryIds")
    List<WishlistItem> findByUserIdAndCategoryIds(@Param("userId") Long userId, @Param("categoryIds") List<Long> categoryIds);
} 