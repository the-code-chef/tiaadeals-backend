package com.tiaadeals.server.controller;

import com.tiaadeals.server.dto.ProductDto;
import com.tiaadeals.server.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Product management
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Create a new product
     */
    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Creates a new product with all details including colors, pricing, and specifications. " +
                     "The category field should be the category name (e.g., 'laptop', 'mobile', 'tv'). " +
                     "Colors array is optional but recommended for products with multiple color variants. " +
                     "Original price should be higher than or equal to the current price."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or category not found",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<ProductDto> createProduct(
            @Parameter(
                description = "Product data to create",
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "Laptop Product",
                            summary = "Create a laptop with colors and specifications",
                            value = """
                            {
                              "name": "mi book 15",
                              "price": 31990,
                              "originalPrice": 51999,
                              "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908106/redmi-book-15_ksizgp.jpg",
                              "colors": [
                                {
                                  "color": "#0000ff",
                                  "colorQuantity": 10
                                },
                                {
                                  "color": "#00ff00",
                                  "colorQuantity": 6
                                },
                                {
                                  "color": "#ff0000",
                                  "colorQuantity": 9
                                }
                              ],
                              "company": "redmi",
                              "description": "For this model, screen size is 39.62 cm and hard disk size is 256 GB. CPU Model Core is i3. RAM Memory Installed Size is 8 GB. Operating System is Windows 10 Home. Special Feature includes Anti Glare Screen, Light Weight, Thin. Graphics Card is Integrated",
                              "category": "laptop",
                              "isShippingAvailable": true,
                              "stock": 25,
                              "reviewCount": 418,
                              "stars": 3.7
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Mobile Product",
                            summary = "Create a mobile phone with minimal data",
                            value = """
                            {
                              "name": "mi 12 pro",
                              "price": 44999,
                              "originalPrice": 79999,
                              "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909725/mi-12-pro_mlm5mt.jpg",
                              "company": "redmi",
                              "description": "For this model, screen size is 6.73 inches and RAM Memory is 8 GB. Operating System is Android 12. Special Feature includes 10bit 2K+ Curved AMOLED Display, 50+50+50MP Flagship Cameras (OIS). This is a 5G device.",
                              "category": "mobile",
                              "isShippingAvailable": true,
                              "stock": 23,
                              "reviewCount": 3294,
                              "stars": 4.0
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Featured Product",
                            summary = "Create a featured product",
                            value = """
                            {
                              "name": "mi watch 2 lite",
                              "price": 2280,
                              "originalPrice": 7999,
                              "featured": true,
                              "image": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909641/mi-watch-2-lite_ofbl7m.jpg",
                              "colors": [
                                {
                                  "color": "#00ff00",
                                  "colorQuantity": 4
                                },
                                {
                                  "color": "#000",
                                  "colorQuantity": 7
                                }
                              ],
                              "company": "redmi",
                              "description": "For this model, screen size is 3.94 cm and rectangular in shape. Special Feature includes Rate Monitor, Oxymeter (SpO2), Music Player, Camera.",
                              "category": "smartwatch",
                              "isShippingAvailable": true,
                              "stock": 21,
                              "reviewCount": 508,
                              "stars": 3.5
                            }
                            """
                        )
                    }
                )
            )
            @Valid @RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Get all products with pagination
     */
    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Retrieves a paginated list of all products. Supports sorting by name, price, stock, stars, and creation date. " +
                     "Use page and size parameters for pagination. Default page size is 20. " +
                     "Products are returned with their complete details including colors, pricing, and specifications."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field (name, price, stock, stars, createdAt)", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc, desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a specific product by its unique identifier. " +
                     "Returns complete product details including colors, pricing, specifications, and category information."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Product retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Update product
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update product",
        description = "Updates an existing product. Only the fields you want to update need to be included in the request body. " +
                     "The category field should be the category name. Colors array can be updated to add, remove, or modify color variants."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated product data")
            @Valid @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete product
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete product",
        description = "Deletes a product by ID. This operation is irreversible and will also remove all associated colors and cart/wishlist items. " +
                     "Use this endpoint with caution as it will cascade delete all related data."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Product deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search products
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search products",
        description = "Search products by name or description. The search is case-insensitive and supports partial matching. " +
                     "For example, searching for 'mi' will find products with 'mi' in the name or description. " +
                     "Supports pagination and sorting."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @Parameter(description = "Search term", example = "laptop")
            @RequestParam String q,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProducts(q, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryName}")
    @Operation(
        summary = "Get products by category",
        description = "Retrieves all products in a specific category. The category name is case-insensitive. " +
                     "Supports pagination and sorting. Useful for category pages and filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @Parameter(description = "Category name", example = "laptop")
            @PathVariable String categoryName,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByCategory(categoryName, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by company
     */
    @GetMapping("/company/{company}")
    @Operation(
        summary = "Get products by company",
        description = "Retrieves all products from a specific company/brand. The company name is case-insensitive. " +
                     "Supports pagination and sorting. Useful for brand pages and filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsByCompany(
            @Parameter(description = "Company name", example = "redmi")
            @PathVariable String company,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByCompany(company, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by price range
     */
    @GetMapping("/price-range")
    @Operation(
        summary = "Get products by price range",
        description = "Retrieves products within a specific price range. Both min and max prices are inclusive. " +
                     "Supports pagination and sorting. Useful for price filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsByPriceRange(
            @Parameter(description = "Minimum price", example = "1000")
            @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price", example = "50000")
            @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by rating range
     */
    @GetMapping("/rating-range")
    @Operation(
        summary = "Get products by rating range",
        description = "Retrieves products within a specific star rating range. Both min and max stars are inclusive. " +
                     "Supports pagination and sorting. Useful for rating-based filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsByRatingRange(
            @Parameter(description = "Minimum stars", example = "3.0")
            @RequestParam Double minStars,
            @Parameter(description = "Maximum stars", example = "5.0")
            @RequestParam Double maxStars,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByRatingRange(minStars, maxStars, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get featured products
     */
    @GetMapping("/featured")
    @Operation(
        summary = "Get featured products",
        description = "Retrieves all featured products. Featured products are typically highlighted on the homepage or special sections. " +
                     "Supports pagination and sorting."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Featured products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getFeaturedProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products in stock
     */
    @GetMapping("/in-stock")
    @Operation(
        summary = "Get products in stock",
        description = "Retrieves all products that have stock available (stock > 0). " +
                     "Supports pagination and sorting. Useful for filtering out-of-stock items."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "In-stock products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsInStock(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsInStock(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products with shipping available
     */
    @GetMapping("/shipping-available")
    @Operation(
        summary = "Get products with shipping available",
        description = "Retrieves all products that have shipping available. " +
                     "Supports pagination and sorting. Useful for filtering products that can be shipped."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Products with shipping available retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsWithShippingAvailable(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsWithShippingAvailable(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products with low stock
     */
    @GetMapping("/low-stock")
    @Operation(
        summary = "Get products with low stock",
        description = "Retrieves products with stock below a specified threshold. Useful for inventory management. " +
                     "Supports pagination and sorting."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Low stock products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        )
    })
    public ResponseEntity<Page<ProductDto>> getProductsWithLowStock(
            @Parameter(description = "Stock threshold", example = "10")
            @RequestParam(defaultValue = "10") Integer threshold,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsWithLowStock(threshold, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Get product statistics
     */
    @GetMapping("/statistics")
    @Operation(
        summary = "Get product statistics",
        description = "Retrieves overall product statistics including total products, products in stock, out of stock, and featured products. " +
                     "Useful for dashboard and analytics."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductService.ProductStatistics.class))
        )
    })
    public ResponseEntity<ProductService.ProductStatistics> getProductStatistics() {
        ProductService.ProductStatistics statistics = productService.getProductStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Update product stock
     */
    @PatchMapping("/{id}/stock")
    @Operation(
        summary = "Update product stock",
        description = "Updates the stock quantity of a product. Use positive values to add stock or negative values to subtract stock. " +
                     "The stock cannot go below 0. Useful for inventory management after sales or restocking."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Stock updated successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid stock quantity (would result in negative stock)",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<ProductDto> updateProductStock(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Quantity to add/subtract", example = "-5")
            @RequestParam Integer quantity) {
        ProductDto updatedProduct = productService.updateProductStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }
} 