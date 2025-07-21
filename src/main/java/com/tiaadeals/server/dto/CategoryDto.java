package com.tiaadeals.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Category operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Schema(description = "Category Data Transfer Object")
public class CategoryDto {

    @JsonProperty("_id")
    @Schema(description = "Unique identifier for the category", example = "1")
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    @JsonProperty("categoryName")
    @Schema(description = "Name of the category", example = "laptop", minLength = 1, maxLength = 100)
    private String categoryName;

    @Size(max = 1000, message = "Category image URL must not exceed 1000 characters")
    @JsonProperty("categoryImage")
    @Schema(description = "URL of the category image", example = "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908106/redmi-book-15_ksizgp.jpg", maxLength = 1000)
    private String categoryImage;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    @Schema(description = "Description of the category", example = "Latest laptops and notebooks", maxLength = 500)
    private String description;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Schema(description = "Timestamp when the category was created", example = "2025-07-19T08:48:46.630Z")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Schema(description = "Timestamp when the category was last updated", example = "2025-07-19T08:48:46.630Z")
    private LocalDateTime updatedAt;

    // Constructors
    public CategoryDto() {}

    public CategoryDto(String categoryName, String categoryImage, String description) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.description = description;
    }

    public CategoryDto(Long id, String categoryName, String categoryImage, String description, 
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "CategoryDto{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", categoryImage='" + categoryImage + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 