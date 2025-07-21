package com.tiaadeals.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Product entity
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Schema(description = "Product information")
public class ProductDto {

    @Schema(description = "Product ID", example = "1")
    @JsonProperty("_id")
    private Long id;

    @Schema(description = "Product name", example = "mi book 15")
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 200, message = "Product name must be between 1 and 200 characters")
    @JsonProperty("name")
    private String name;

    @Schema(description = "Product price", example = "31990")
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @JsonProperty("price")
    private BigDecimal price;

    @Schema(description = "Original price before discount", example = "51999")
    @NotNull(message = "Original price is required")
    @DecimalMin(value = "0.01", message = "Original price must be greater than 0")
    @JsonProperty("originalPrice")
    private BigDecimal originalPrice;

    @Schema(description = "Product image URL", example = "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908106/redmi-book-15_ksizgp.jpg")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @JsonProperty("image")
    private String image;

    @Schema(description = "Product colors with quantities")
    @JsonProperty("colors")
    private List<ProductColorDto> colors;

    @Schema(description = "Product company/brand", example = "redmi")
    @Size(max = 100, message = "Company must not exceed 100 characters")
    @JsonProperty("company")
    private String company;

    @Schema(description = "Product description", example = "For this model, screen size is 39.62 cm and hard disk size is 256 GB...")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @JsonProperty("description")
    private String description;

    @Schema(description = "Product category name", example = "laptop")
    @NotBlank(message = "Category is required")
    @JsonProperty("category")
    private String category;

    @Schema(description = "Whether shipping is available", example = "true")
    @JsonProperty("isShippingAvailable")
    private Boolean isShippingAvailable = true;

    @Schema(description = "Available stock quantity", example = "25")
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @JsonProperty("stock")
    private Integer stock;

    @Schema(description = "Number of reviews", example = "418")
    @JsonProperty("reviewCount")
    private Integer reviewCount = 0;

    @Schema(description = "Product rating (stars)", example = "3.7")
    @JsonProperty("stars")
    private Double stars = 0.0;

    @Schema(description = "Whether product is featured", example = "false")
    @JsonProperty("featured")
    private Boolean featured = false;

    // Additional fields for internal use
    @Schema(description = "Category ID (internal use)")
    private Long categoryId;

    @Schema(description = "Whether product is active", example = "true")
    private Boolean isActive = true;

    @Schema(description = "Product SKU", example = "MI-BOOK-15-001")
    private String sku;

    @Schema(description = "Product brand", example = "Redmi")
    private String brand;

    @Schema(description = "Product weight in grams", example = "1500.0")
    private Double weight;

    @Schema(description = "Product dimensions", example = "35.6 x 24.2 x 1.8 cm")
    private String dimensions;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // Constructors
    public ProductDto() {}

    public ProductDto(Long id, String name, BigDecimal price, BigDecimal originalPrice, String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.category = category;
        this.stock = stock;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ProductColorDto> getColors() {
        return colors;
    }

    public void setColors(List<ProductColorDto> colors) {
        this.colors = colors;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsShippingAvailable() {
        return isShippingAvailable;
    }

    public void setIsShippingAvailable(Boolean isShippingAvailable) {
        this.isShippingAvailable = isShippingAvailable;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isInStock() {
        return stock != null && stock > 0;
    }

    public boolean isLowStock(int threshold) {
        return stock != null && stock > 0 && stock <= threshold;
    }

    public boolean isOutOfStock() {
        return stock == null || stock <= 0;
    }

    public boolean hasDiscount() {
        return originalPrice != null && price != null && originalPrice.compareTo(price) > 0;
    }

    public BigDecimal getDiscountPercentage() {
        if (hasDiscount()) {
            return originalPrice.subtract(price)
                    .divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", originalPrice=" + originalPrice +
                ", company='" + company + '\'' +
                ", category='" + category + '\'' +
                ", stock=" + stock +
                ", isShippingAvailable=" + isShippingAvailable +
                ", featured=" + featured +
                ", reviewCount=" + reviewCount +
                ", stars=" + stars +
                '}';
    }
} 