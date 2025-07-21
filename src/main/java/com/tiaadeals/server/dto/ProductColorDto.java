package com.tiaadeals.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for product color information
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Schema(description = "Product color information")
public class ProductColorDto {

    @Schema(description = "Color hex code (e.g., #0000ff)", example = "#0000ff")
    @NotBlank(message = "Color is required")
    private String color;

    @Schema(description = "Quantity available for this color", example = "10")
    @NotNull(message = "Color quantity is required")
    @Min(value = 0, message = "Color quantity cannot be negative")
    private Integer colorQuantity;

    // Constructors
    public ProductColorDto() {}

    public ProductColorDto(String color, Integer colorQuantity) {
        this.color = color;
        this.colorQuantity = colorQuantity;
    }

    // Getters and Setters
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getColorQuantity() {
        return colorQuantity;
    }

    public void setColorQuantity(Integer colorQuantity) {
        this.colorQuantity = colorQuantity;
    }

    @Override
    public String toString() {
        return "ProductColorDto{" +
                "color='" + color + '\'' +
                ", colorQuantity=" + colorQuantity +
                '}';
    }
} 