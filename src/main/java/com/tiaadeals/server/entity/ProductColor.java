package com.tiaadeals.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ProductColor entity representing color variants of products
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Entity
@Table(name = "product_colors")
public class ProductColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Color is required")
    @Column(nullable = false, length = 20)
    private String color;

    @NotNull(message = "Color quantity is required")
    @Min(value = 0, message = "Color quantity cannot be negative")
    @Column(name = "color_quantity", nullable = false)
    private Integer colorQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Constructors
    public ProductColor() {}

    public ProductColor(String color, Integer colorQuantity, Product product) {
        this.color = color;
        this.colorQuantity = colorQuantity;
        this.product = product;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "ProductColor{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", colorQuantity=" + colorQuantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductColor that = (ProductColor) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 