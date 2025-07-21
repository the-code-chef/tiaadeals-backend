package com.tiaadeals.server.repository;

import com.tiaadeals.server.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by name (case-insensitive)
     * 
     * @param name the product name to search for
     * @return Optional containing the product if found
     */
    Optional<Product> findByNameIgnoreCase(String name);

    /**
     * Check if a product exists with the given name
     * 
     * @param name the product name to check
     * @return true if product exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find products by category name (case-insensitive)
     * 
     * @param categoryName the category name to filter by
     * @param pageable pagination and sorting parameters
     * @return page of products in the specified category
     */
    Page<Product> findByCategoryCategoryNameIgnoreCase(String categoryName, Pageable pageable);

    /**
     * Find products by category name without pagination
     * 
     * @param categoryName the category name to filter by
     * @return list of products in the specified category
     */
    List<Product> findByCategoryCategoryNameIgnoreCase(String categoryName);

    /**
     * Find products by price range
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination and sorting parameters
     * @return page of products within the price range
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Find products with stock greater than zero
     * 
     * @param stock the minimum stock quantity
     * @param pageable pagination and sorting parameters
     * @return page of products in stock
     */
    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    /**
     * Find products that are out of stock
     * 
     * @param stock the stock quantity (usually 0)
     * @param pageable pagination and sorting parameters
     * @return page of products out of stock
     */
    Page<Product> findByStockLessThanEqual(Integer stock, Pageable pageable);

    /**
     * Find products with low stock (between 0 and threshold)
     * 
     * @param threshold the stock threshold
     * @param minStock the minimum stock (usually 0)
     * @param pageable pagination and sorting parameters
     * @return page of products with low stock
     */
    Page<Product> findByStockLessThanEqualAndStockGreaterThan(Integer threshold, Integer minStock, Pageable pageable);

    /**
     * Find products by company (case-insensitive)
     * 
     * @param company the company name
     * @param pageable pagination and sorting parameters
     * @return page of products by company
     */
    Page<Product> findByCompanyIgnoreCase(String company, Pageable pageable);

    /**
     * Find products with shipping available
     * 
     * @param pageable pagination and sorting parameters
     * @return page of products with shipping available
     */
    Page<Product> findByIsShippingAvailableTrue(Pageable pageable);

    /**
     * Find products by rating range
     * 
     * @param minStars minimum stars
     * @param maxStars maximum stars
     * @param pageable pagination and sorting parameters
     * @return page of products within rating range
     */
    Page<Product> findByStarsBetween(Double minStars, Double maxStars, Pageable pageable);

    /**
     * Find products by name or description (case-insensitive)
     * 
     * @param name the name search term
     * @param description the description search term
     * @param pageable pagination and sorting parameters
     * @return page of products matching the search terms
     */
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    /**
     * Find products by name pattern (for search functionality)
     * 
     * @param namePattern the name pattern to search for (supports % wildcards)
     * @param pageable pagination and sorting parameters
     * @return page of products matching the name pattern
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(%:namePattern%)")
    Page<Product> findByNamePattern(@Param("namePattern") String namePattern, Pageable pageable);

    /**
     * Find products by description pattern
     * 
     * @param descriptionPattern the description pattern to search for
     * @param pageable pagination and sorting parameters
     * @return page of products matching the description pattern
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(%:namePattern%)")
    Page<Product> findByDescriptionPattern(@Param("namePattern") String descriptionPattern, Pageable pageable);

    /**
     * Full-text search for products (name and description)
     * 
     * @param searchTerm the search term to look for in name or description
     * @param pageable pagination and sorting parameters
     * @return page of products matching the search term
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(%:searchTerm%) OR LOWER(p.description) LIKE LOWER(%:searchTerm%)")
    Page<Product> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find products by category and price range
     * 
     * @param categoryId the category ID to filter by
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination and sorting parameters
     * @return page of products in category within price range
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByCategoryAndPriceRange(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Find products by category and search term
     * 
     * @param categoryId the category ID to filter by
     * @param searchTerm the search term to look for
     * @param pageable pagination and sorting parameters
     * @return page of products in category matching search term
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND (LOWER(p.name) LIKE LOWER(%:searchTerm%) OR LOWER(p.description) LIKE LOWER(%:searchTerm%))")
    Page<Product> findByCategoryAndSearchTerm(
            @Param("categoryId") Long categoryId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    /**
     * Find products with low stock (below threshold)
     * 
     * @param threshold the stock threshold
     * @param pageable pagination and sorting parameters
     * @return page of products with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold")
    Page<Product> findProductsWithLowStock(@Param("threshold") Integer threshold, Pageable pageable);

    /**
     * Find products created after a specific date
     * 
     * @param date the date to filter by
     * @param pageable pagination and sorting parameters
     * @return page of products created after the specified date
     */
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :date")
    Page<Product> findProductsCreatedAfter(@Param("date") java.time.LocalDateTime date, Pageable pageable);

    /**
     * Find products updated after a specific date
     * 
     * @param date the date to filter by
     * @param pageable pagination and sorting parameters
     * @return page of products updated after the specified date
     */
    @Query("SELECT p FROM Product p WHERE p.updatedAt >= :date")
    Page<Product> findProductsUpdatedAfter(@Param("date") java.time.LocalDateTime date, Pageable pageable);

    /**
     * Find products with highest price
     * 
     * @param limit the number of products to return
     * @return list of products with highest prices
     */
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findTopProductsByPrice(@Param("limit") int limit);

    /**
     * Find products with lowest price
     * 
     * @param limit the number of products to return
     * @return list of products with lowest prices
     */
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findTopProductsByLowestPrice(@Param("limit") int limit);

    /**
     * Find products with most stock
     * 
     * @param limit the number of products to return
     * @return list of products with most stock
     */
    @Query("SELECT p FROM Product p ORDER BY p.stock DESC")
    List<Product> findTopProductsByStock(@Param("limit") int limit);

    /**
     * Find products recently added
     * 
     * @param limit the number of products to return
     * @return list of recently added products
     */
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findRecentlyAddedProducts(@Param("limit") int limit);

    /**
     * Find products recently updated
     * 
     * @param limit the number of products to return
     * @return list of recently updated products
     */
    @Query("SELECT p FROM Product p ORDER BY p.updatedAt DESC")
    List<Product> findRecentlyUpdatedProducts(@Param("limit") int limit);

    /**
     * Count total number of products
     * 
     * @return total product count
     */
    @Query("SELECT COUNT(p) FROM Product p")
    long countTotalProducts();

    /**
     * Count products by category
     * 
     * @param categoryId the category ID to count products for
     * @return number of products in the category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsByCategory(@Param("categoryId") Long categoryId);

    /**
     * Count products in stock
     * 
     * @return number of products with stock > 0
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    long countProductsInStock();

    /**
     * Count products out of stock
     * 
     * @return number of products with stock = 0
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock = 0")
    long countProductsOutOfStock();

    /**
     * Find products with pagination and sorting
     * 
     * @param pageable pagination and sorting parameters
     * @return page of products
     */
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    Page<Product> findAllProductsWithPagination(Pageable pageable);

    /**
     * Find products by multiple categories
     * 
     * @param categoryIds list of category IDs to filter by
     * @param pageable pagination and sorting parameters
     * @return page of products in the specified categories
     */
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    Page<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    /**
     * Find featured products
     * 
     * @param pageable pagination and sorting parameters
     * @return page of featured products
     */
    Page<Product> findByIsFeaturedTrue(Pageable pageable);

    /**
     * Count products with stock greater than specified value
     * 
     * @param stock the minimum stock quantity
     * @return number of products with stock greater than specified value
     */
    long countByStockGreaterThan(Integer stock);

    /**
     * Count products with stock less than or equal to specified value
     * 
     * @param stock the maximum stock quantity
     * @return number of products with stock less than or equal to specified value
     */
    long countByStockLessThanEqual(Integer stock);

    /**
     * Count featured products
     * 
     * @return number of featured products
     */
    long countByIsFeaturedTrue();
} 