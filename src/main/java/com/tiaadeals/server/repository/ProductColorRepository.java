package com.tiaadeals.server.repository;

import com.tiaadeals.server.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ProductColor entity
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {

    /**
     * Find all colors for a specific product
     * 
     * @param productId the product ID
     * @return list of product colors
     */
    List<ProductColor> findByProductId(Long productId);

    /**
     * Find colors by color value
     * 
     * @param color the color hex code
     * @return list of product colors
     */
    List<ProductColor> findByColor(String color);

    /**
     * Find colors with quantity greater than specified value
     * 
     * @param quantity the minimum quantity
     * @return list of product colors
     */
    List<ProductColor> findByColorQuantityGreaterThan(Integer quantity);

    /**
     * Find colors with quantity less than specified value (low stock)
     * 
     * @param quantity the maximum quantity
     * @return list of product colors
     */
    List<ProductColor> findByColorQuantityLessThan(Integer quantity);

    /**
     * Find out of stock colors (quantity = 0)
     * 
     * @return list of out of stock colors
     */
    List<ProductColor> findByColorQuantityEquals(Integer quantity);

    /**
     * Delete all colors for a specific product
     * 
     * @param productId the product ID
     */
    void deleteByProductId(Long productId);

    /**
     * Count colors for a specific product
     * 
     * @param productId the product ID
     * @return number of colors
     */
    long countByProductId(Long productId);

    /**
     * Find colors by product ID and color value
     * 
     * @param productId the product ID
     * @param color the color hex code
     * @return list of matching colors
     */
    List<ProductColor> findByProductIdAndColor(Long productId, String color);

    /**
     * Find products with specific color and minimum quantity
     * 
     * @param color the color hex code
     * @param minQuantity the minimum quantity
     * @return list of product colors
     */
    @Query("SELECT pc FROM ProductColor pc WHERE pc.color = :color AND pc.colorQuantity >= :minQuantity")
    List<ProductColor> findByColorAndMinQuantity(@Param("color") String color, @Param("minQuantity") Integer minQuantity);
} 