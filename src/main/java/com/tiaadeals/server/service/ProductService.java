package com.tiaadeals.server.service;

import com.tiaadeals.server.dto.ProductColorDto;
import com.tiaadeals.server.dto.ProductDto;
import com.tiaadeals.server.entity.Category;
import com.tiaadeals.server.entity.Product;
import com.tiaadeals.server.entity.ProductColor;
import com.tiaadeals.server.exception.ResourceNotFoundException;
import com.tiaadeals.server.repository.CategoryRepository;
import com.tiaadeals.server.repository.ProductColorRepository;
import com.tiaadeals.server.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Product business logic
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    /**
     * Create a new product
     * 
     * @param productDto the product data
     * @return the created product DTO
     * @throws ResourceNotFoundException if category not found
     */
    public ProductDto createProduct(ProductDto productDto) {
        // Find category by name
        Category category = categoryRepository.findByCategoryNameIgnoreCase(productDto.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + productDto.getCategory()));

        // Create new product entity
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setOriginalPrice(productDto.getOriginalPrice());
        product.setImageUrl(productDto.getImage());
        product.setCompany(productDto.getCompany());
        product.setCategory(category);
        product.setStock(productDto.getStock());
        product.setIsShippingAvailable(productDto.getIsShippingAvailable() != null ? productDto.getIsShippingAvailable() : true);
        product.setIsFeatured(productDto.getFeatured() != null ? productDto.getFeatured() : false);
        product.setReviewCount(productDto.getReviewCount() != null ? productDto.getReviewCount() : 0);
        product.setStars(productDto.getStars() != null ? productDto.getStars() : 0.0);
        product.setIsActive(true);
        product.setSku(productDto.getSku());
        product.setBrand(productDto.getBrand());
        product.setWeight(productDto.getWeight());
        product.setDimensions(productDto.getDimensions());

        // Save product first to get the ID
        Product savedProduct = productRepository.save(product);

        // Add colors if provided
        if (productDto.getColors() != null && !productDto.getColors().isEmpty()) {
            List<ProductColor> colors = productDto.getColors().stream()
                    .map(colorDto -> new ProductColor(colorDto.getColor(), colorDto.getColorQuantity(), savedProduct))
                    .collect(Collectors.toList());
            productColorRepository.saveAll(colors);
            savedProduct.setColors(colors);
        }

        return convertToDto(savedProduct);
    }

    /**
     * Get product by ID
     * 
     * @param id the product ID
     * @return the product DTO
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDto(product);
    }

    /**
     * Update product
     * 
     * @param id the product ID
     * @param productDto the updated product data
     * @return the updated product DTO
     * @throws ResourceNotFoundException if product or category not found
     */
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields
        if (productDto.getName() != null) {
            product.setName(productDto.getName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }
        if (productDto.getOriginalPrice() != null) {
            product.setOriginalPrice(productDto.getOriginalPrice());
        }
        if (productDto.getImage() != null) {
            product.setImageUrl(productDto.getImage());
        }
        if (productDto.getCompany() != null) {
            product.setCompany(productDto.getCompany());
        }
        if (productDto.getStock() != null) {
            product.setStock(productDto.getStock());
        }
        if (productDto.getIsShippingAvailable() != null) {
            product.setIsShippingAvailable(productDto.getIsShippingAvailable());
        }
        if (productDto.getFeatured() != null) {
            product.setIsFeatured(productDto.getFeatured());
        }
        if (productDto.getReviewCount() != null) {
            product.setReviewCount(productDto.getReviewCount());
        }
        if (productDto.getStars() != null) {
            product.setStars(productDto.getStars());
        }
        if (productDto.getSku() != null) {
            product.setSku(productDto.getSku());
        }
        if (productDto.getBrand() != null) {
            product.setBrand(productDto.getBrand());
        }
        if (productDto.getWeight() != null) {
            product.setWeight(productDto.getWeight());
        }
        if (productDto.getDimensions() != null) {
            product.setDimensions(productDto.getDimensions());
        }

        // Update category if provided
        if (productDto.getCategory() != null) {
            Category category = categoryRepository.findByCategoryNameIgnoreCase(productDto.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + productDto.getCategory()));
            product.setCategory(category);
        }

        // Update colors if provided
        if (productDto.getColors() != null) {
            // Delete existing colors
            productColorRepository.deleteByProductId(id);
            
            // Add new colors
            if (!productDto.getColors().isEmpty()) {
                List<ProductColor> colors = productDto.getColors().stream()
                        .map(colorDto -> new ProductColor(colorDto.getColor(), colorDto.getColorQuantity(), product))
                        .collect(Collectors.toList());
                productColorRepository.saveAll(colors);
                product.setColors(colors);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    /**
     * Delete product
     * 
     * @param id the product ID
     * @throws ResourceNotFoundException if product not found
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        // Colors will be deleted automatically due to cascade
        productRepository.deleteById(id);
    }

    /**
     * Get all products with pagination
     * 
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    /**
     * Get all products
     * 
     * @return list of product DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get products by category
     * 
     * @param categoryName the category name
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(String categoryName, Pageable pageable) {
        return productRepository.findByCategoryCategoryNameIgnoreCase(categoryName, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products by category
     * 
     * @param categoryName the category name
     * @return list of product DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryCategoryNameIgnoreCase(categoryName)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Search products by name or description
     * 
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products by price range
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products by company
     * 
     * @param company the company name
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCompany(String company, Pageable pageable) {
        return productRepository.findByCompanyIgnoreCase(company, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products in stock
     * 
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsInStock(Pageable pageable) {
        return productRepository.findByStockGreaterThan(0, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products out of stock
     * 
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsOutOfStock(Pageable pageable) {
        return productRepository.findByStockLessThanEqual(0, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products with low stock
     * 
     * @param threshold the stock threshold
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsWithLowStock(Integer threshold, Pageable pageable) {
        return productRepository.findByStockLessThanEqualAndStockGreaterThan(threshold, 0, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get featured products
     * 
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByIsFeaturedTrue(pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products with shipping available
     * 
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsWithShippingAvailable(Pageable pageable) {
        return productRepository.findByIsShippingAvailableTrue(pageable)
                .map(this::convertToDto);
    }

    /**
     * Get products by rating range
     * 
     * @param minStars minimum stars
     * @param maxStars maximum stars
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByRatingRange(Double minStars, Double maxStars, Pageable pageable) {
        return productRepository.findByStarsBetween(minStars, maxStars, pageable)
                .map(this::convertToDto);
    }

    /**
     * Update product stock
     * 
     * @param id the product ID
     * @param quantity the quantity to add/subtract
     * @return the updated product DTO
     * @throws ResourceNotFoundException if product not found
     */
    public ProductDto updateProductStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    /**
     * Get product statistics
     * 
     * @return product statistics
     */
    @Transactional(readOnly = true)
    public ProductStatistics getProductStatistics() {
        long totalProducts = productRepository.count();
        long productsInStock = productRepository.countByStockGreaterThan(0);
        long productsOutOfStock = productRepository.countByStockLessThanEqual(0);
        long featuredProducts = productRepository.countByIsFeaturedTrue();

        return new ProductStatistics(totalProducts, productsInStock, productsOutOfStock, featuredProducts);
    }

    /**
     * Convert Product entity to ProductDto
     * 
     * @param product the product entity
     * @return the product DTO
     */
    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setOriginalPrice(product.getOriginalPrice());
        productDto.setImage(product.getImageUrl());
        productDto.setCompany(product.getCompany());
        productDto.setCategory(product.getCategory().getCategoryName());
        productDto.setStock(product.getStock());
        productDto.setIsShippingAvailable(product.getIsShippingAvailable());
        productDto.setFeatured(product.getIsFeatured());
        productDto.setReviewCount(product.getReviewCount());
        productDto.setStars(product.getStars());
        productDto.setIsActive(product.getIsActive());
        productDto.setSku(product.getSku());
        productDto.setBrand(product.getBrand());
        productDto.setWeight(product.getWeight());
        productDto.setDimensions(product.getDimensions());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());

        // Convert colors
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            List<ProductColorDto> colorDtos = product.getColors().stream()
                    .map(color -> new ProductColorDto(color.getColor(), color.getColorQuantity()))
                    .collect(Collectors.toList());
            productDto.setColors(colorDtos);
        }

        return productDto;
    }

    /**
     * Inner class for product statistics
     */
    public static class ProductStatistics {
        private final long totalProducts;
        private final long productsInStock;
        private final long productsOutOfStock;
        private final long featuredProducts;

        public ProductStatistics(long totalProducts, long productsInStock, long productsOutOfStock, long featuredProducts) {
            this.totalProducts = totalProducts;
            this.productsInStock = productsInStock;
            this.productsOutOfStock = productsOutOfStock;
            this.featuredProducts = featuredProducts;
        }

        public long getTotalProducts() {
            return totalProducts;
        }

        public long getProductsInStock() {
            return productsInStock;
        }

        public long getProductsOutOfStock() {
            return productsOutOfStock;
        }

        public long getFeaturedProducts() {
            return featuredProducts;
        }
    }
} 