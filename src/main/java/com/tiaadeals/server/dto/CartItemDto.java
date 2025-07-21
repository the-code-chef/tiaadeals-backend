package com.tiaadeals.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for CartItem entity
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
public class CartItemDto {

    @JsonProperty("id")
    private Long id;

    @NotNull(message = "User ID is required")
    @JsonProperty("userId")
    private Long userId;

    @NotNull(message = "Product ID is required")
    @JsonProperty("productId")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("productPrice")
    private BigDecimal productPrice;

    @JsonProperty("productImageUrl")
    private String productImageUrl;

    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;

    @JsonProperty("isInStock")
    private Boolean isInStock;

    @JsonProperty("availableStock")
    private Integer availableStock;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Constructors
    public CartItemDto() {}

    public CartItemDto(Long id, Long userId, Long productId, Integer quantity) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Boolean getIsInStock() {
        return isInStock;
    }

    public void setIsInStock(Boolean isInStock) {
        this.isInStock = isInStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
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
    public void calculateTotalPrice() {
        if (this.productPrice != null && this.quantity != null) {
            this.totalPrice = this.productPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public boolean isLowStock(int threshold) {
        return this.availableStock != null && this.availableStock <= threshold && this.availableStock > 0;
    }

    public boolean isOutOfStock() {
        return this.availableStock != null && this.availableStock == 0;
    }

    @Override
    public String toString() {
        return "CartItemDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", totalPrice=" + totalPrice +
                ", isInStock=" + isInStock +
                ", availableStock=" + availableStock +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 