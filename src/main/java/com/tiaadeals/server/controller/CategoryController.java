package com.tiaadeals.server.controller;

import com.tiaadeals.server.dto.CategoryDto;
import com.tiaadeals.server.service.CategoryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "APIs for managing product categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all categories
     */
    @GetMapping
    @Operation(
        summary = "Get all categories",
        description = "Retrieves a list of all available product categories. " +
                     "Categories are returned in ascending order by category name. " +
                     "Each category includes its ID, name, image URL, description, and timestamps. " +
                     "This endpoint is useful for populating category dropdowns or navigation menus."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get category by ID",
        description = "Retrieves a specific category by its unique identifier. " +
                     "The ID is a sequential number assigned when the category was created. " +
                     "Returns 404 if the category with the specified ID does not exist."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Category retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<CategoryDto> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Get category by name
     */
    @GetMapping("/name/{categoryName}")
    @Operation(
        summary = "Get category by name",
        description = "Retrieves a specific category by its name (case-insensitive). " +
                     "This endpoint is useful when you know the exact category name. " +
                     "The search is case-insensitive, so 'Laptop', 'laptop', and 'LAPTOP' will all find the same category. " +
                     "Returns 404 if the category with the specified name does not exist."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Category retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<CategoryDto> getCategoryByName(
            @Parameter(description = "Category name", example = "laptop")
            @PathVariable String categoryName) {
        CategoryDto category = categoryService.getCategoryByName(categoryName);
        return ResponseEntity.ok(category);
    }

    /**
     * Create a new category
     */
    @PostMapping
    @Operation(
        summary = "Create a new category",
        description = "Creates a new product category. Category name must be unique and cannot be duplicated. " +
                     "The categoryName field is required and must be between 1-100 characters. " +
                     "The categoryImage field is optional and should be a valid URL. " +
                     "The description field is optional and limited to 500 characters."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or category name already exists",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<CategoryDto> createCategory(
            @Parameter(description = "Category data to create")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Category data to create",
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "Basic Category",
                            summary = "Create a category with minimal data",
                            value = """
                            {
                              "categoryName": "laptop",
                              "description": "Latest laptops and notebooks"
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Complete Category",
                            summary = "Create a category with all fields",
                            value = """
                            {
                              "categoryName": "smartphone",
                              "categoryImage": "https://res.cloudinary.com/dtbd1y4en/image/upload/v1683957585/oneplus-nord-CE-3-lite_weksou.jpg",
                              "description": "Latest smartphones and mobile devices"
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Invalid Category",
                            summary = "Example that would cause validation errors",
                            value = """
                            {
                              "categoryName": "",
                              "categoryImage": "invalid-url",
                              "description": "This description is way too long and exceeds the maximum allowed length of 500 characters. " +
                                            "It should be rejected by the validation system. " +
                                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                                            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                                            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                                            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                            }
                            """
                        )
                    }
                )
            )
            @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * Update an existing category
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing category",
        description = "Updates an existing category by ID. Category name must remain unique. " +
                     "Only the fields you want to update need to be included in the request body. " +
                     "The categoryName field must be between 1-100 characters if provided. " +
                     "The categoryImage field should be a valid URL if provided. " +
                     "The description field is limited to 500 characters if provided."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or category name already exists",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<CategoryDto> updateCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated category data")
            @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Delete a category
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a category",
        description = "Deletes a category by ID. This operation is irreversible and will also remove all associated products. " +
                     "Use this endpoint with caution as it will cascade delete all products in the category. " +
                     "Returns 204 (No Content) on successful deletion. " +
                     "Returns 404 if the category with the specified ID does not exist."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Category deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search categories
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search categories",
        description = "Search categories by name or description. The search is case-insensitive and supports partial matching. " +
                     "For example, searching for 'lap' will find categories with 'laptop' in the name or description. " +
                     "The search term should be at least 1 character long."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<List<CategoryDto>> searchCategories(
            @Parameter(description = "Search term", example = "laptop")
            @RequestParam String q) {
        List<CategoryDto> categories = categoryService.searchCategories(q);
        return ResponseEntity.ok(categories);
    }

    /**
     * Get categories with products
     */
    @GetMapping("/with-products")
    @Operation(
        summary = "Get categories with products",
        description = "Retrieves only categories that have associated products. " +
                     "This endpoint is useful for filtering out empty categories that don't have any products yet. " +
                     "Categories are returned in ascending order by category name. " +
                     "Useful for displaying only active categories in navigation menus."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<List<CategoryDto>> getCategoriesWithProducts() {
        List<CategoryDto> categories = categoryService.getCategoriesWithProducts();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get categories without products
     */
    @GetMapping("/without-products")
    @Operation(
        summary = "Get categories without products",
        description = "Retrieves only categories that have no associated products. " +
                     "This endpoint is useful for identifying empty categories that might need products added. " +
                     "Categories are returned in ascending order by category name. " +
                     "Useful for admin dashboards to identify categories that need attention."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryDto.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<List<CategoryDto>> getCategoriesWithoutProducts() {
        List<CategoryDto> categories = categoryService.getCategoriesWithoutProducts();
        return ResponseEntity.ok(categories);
    }

    /**
     * Check if category exists by name
     */
    @GetMapping("/exists/{categoryName}")
    @Operation(
        summary = "Check if category exists by name",
        description = "Checks if a category with the given name exists (case-insensitive). " +
                     "This endpoint is useful for validation before creating a new category. " +
                     "Returns true if the category exists, false otherwise. " +
                     "The search is case-insensitive, so 'Laptop', 'laptop', and 'LAPTOP' will all return the same result."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Check completed successfully",
            content = @Content(schema = @Schema(implementation = Boolean.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Boolean> existsByCategoryName(
            @Parameter(description = "Category name", example = "laptop")
            @PathVariable String categoryName) {
        boolean exists = categoryService.existsByCategoryName(categoryName);
        return ResponseEntity.ok(exists);
    }
} 