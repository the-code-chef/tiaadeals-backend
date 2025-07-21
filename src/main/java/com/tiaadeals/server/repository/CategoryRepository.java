package com.tiaadeals.server.repository;

import com.tiaadeals.server.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name (case-insensitive)
     * 
     * @param categoryName the category name to search for
     * @return Optional containing the category if found
     */
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    /**
     * Check if a category exists with the given name
     * 
     * @param categoryName the category name to check
     * @return true if category exists, false otherwise
     */
    boolean existsByCategoryNameIgnoreCase(String categoryName);

    /**
     * Find categories by name pattern (for search functionality)
     * 
     * @param namePattern the name pattern to search for (supports % wildcards)
     * @return list of categories matching the name pattern
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.categoryName) LIKE LOWER(%:namePattern%)")
    List<Category> findByCategoryNamePattern(@Param("namePattern") String namePattern);

    /**
     * Find categories by description pattern
     * 
     * @param descriptionPattern the description pattern to search for
     * @return list of categories matching the description pattern
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.description) LIKE LOWER(%:descriptionPattern%)")
    List<Category> findByDescriptionPattern(@Param("descriptionPattern") String descriptionPattern);

    /**
     * Find categories with products
     * 
     * @return list of categories that have associated products
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p")
    List<Category> findCategoriesWithProducts();

    /**
     * Find categories without products
     * 
     * @return list of categories that have no associated products
     */
    @Query("SELECT c FROM Category c WHERE c.products IS EMPTY")
    List<Category> findCategoriesWithoutProducts();

    /**
     * Find categories with product count
     * 
     * @return list of categories with their product counts
     */
    @Query("SELECT c, COUNT(p) as productCount FROM Category c LEFT JOIN c.products p GROUP BY c")
    List<Object[]> findCategoriesWithProductCount();

    /**
     * Find categories created after a specific date
     * 
     * @param date the date to filter by
     * @return list of categories created after the specified date
     */
    @Query("SELECT c FROM Category c WHERE c.createdAt >= :date")
    List<Category> findCategoriesCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find categories with pagination and sorting
     * 
     * @param pageable pagination and sorting parameters
     * @return page of categories
     */
    @Query("SELECT c FROM Category c ORDER BY c.categoryName ASC")
    org.springframework.data.domain.Page<Category> findAllCategoriesWithPagination(org.springframework.data.domain.Pageable pageable);

    /**
     * Find categories by name or description (for search functionality)
     * 
     * @param searchTerm the search term to look for in name or description
     * @return list of categories matching the search term
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.categoryName) LIKE LOWER(%:searchTerm%) OR LOWER(c.description) LIKE LOWER(%:searchTerm%)")
    List<Category> findByCategoryNameOrDescriptionContaining(@Param("searchTerm") String searchTerm);

    /**
     * Count total number of categories
     * 
     * @return total category count
     */
    @Query("SELECT COUNT(c) FROM Category c")
    long countTotalCategories();

    /**
     * Find categories with most products (top N)
     * 
     * @param limit the number of categories to return
     * @return list of categories with the most products
     */
    @Query("SELECT c FROM Category c JOIN c.products p GROUP BY c ORDER BY COUNT(p) DESC")
    List<Category> findTopCategoriesByProductCount(@Param("limit") int limit);
} 