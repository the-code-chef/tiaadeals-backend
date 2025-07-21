package com.tiaadeals.server.service;

import com.tiaadeals.server.dto.CategoryDto;
import com.tiaadeals.server.entity.Category;
import com.tiaadeals.server.exception.ResourceNotFoundException;
import com.tiaadeals.server.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Category business logic
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories
     * 
     * @return list of all categories
     */
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     * 
     * @param id the category ID
     * @return category DTO
     * @throws ResourceNotFoundException if category not found
     */
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDto(category);
    }

    /**
     * Get category by name
     * 
     * @param categoryName the category name
     * @return category DTO
     * @throws ResourceNotFoundException if category not found
     */
    public CategoryDto getCategoryByName(String categoryName) {
        Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + categoryName));
        return convertToDto(category);
    }

    /**
     * Create a new category
     * 
     * @param categoryDto the category data
     * @return created category DTO
     */
    public CategoryDto createCategory(CategoryDto categoryDto) {
        // Check if category with same name already exists
        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryDto.getCategoryName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getCategoryName() + "' already exists");
        }

        Category category = new Category(
                categoryDto.getCategoryName(),
                categoryDto.getCategoryImage(),
                categoryDto.getDescription()
        );

        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    /**
     * Update an existing category
     * 
     * @param id the category ID
     * @param categoryDto the updated category data
     * @return updated category DTO
     * @throws ResourceNotFoundException if category not found
     */
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if new name conflicts with existing category (excluding current category)
        if (!existingCategory.getCategoryName().equalsIgnoreCase(categoryDto.getCategoryName()) &&
            categoryRepository.existsByCategoryNameIgnoreCase(categoryDto.getCategoryName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getCategoryName() + "' already exists");
        }

        existingCategory.setCategoryName(categoryDto.getCategoryName());
        existingCategory.setCategoryImage(categoryDto.getCategoryImage());
        existingCategory.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDto(updatedCategory);
    }

    /**
     * Delete a category
     * 
     * @param id the category ID
     * @throws ResourceNotFoundException if category not found
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Search categories by name or description
     * 
     * @param searchTerm the search term
     * @return list of matching categories
     */
    public List<CategoryDto> searchCategories(String searchTerm) {
        List<Category> categories = categoryRepository.findByCategoryNameOrDescriptionContaining(searchTerm);
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get categories with products
     * 
     * @return list of categories that have associated products
     */
    public List<CategoryDto> getCategoriesWithProducts() {
        List<Category> categories = categoryRepository.findCategoriesWithProducts();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get categories without products
     * 
     * @return list of categories that have no associated products
     */
    public List<CategoryDto> getCategoriesWithoutProducts() {
        List<Category> categories = categoryRepository.findCategoriesWithoutProducts();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if category exists by name
     * 
     * @param categoryName the category name
     * @return true if exists, false otherwise
     */
    public boolean existsByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryNameIgnoreCase(categoryName);
    }

    /**
     * Convert Category entity to CategoryDto
     * 
     * @param category the category entity
     * @return category DTO
     */
    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getCategoryName(),
                category.getCategoryImage(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
} 