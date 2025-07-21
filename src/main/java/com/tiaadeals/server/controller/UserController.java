package com.tiaadeals.server.controller;

import com.tiaadeals.server.dto.UserDto;
import com.tiaadeals.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for User management operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management APIs")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get all users with pagination
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return page of users
     */
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a paginated list of all users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid pagination parameters",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<UserDto> users = userService.getAllUsers(pageable);
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user by ID
     * 
     * @param id the user ID
     * @return the user
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by email
     * 
     * @param email the user email
     * @return the user
     */
    @GetMapping("/email/{email}")
    @Operation(
        summary = "Get user by email",
        description = "Retrieves a specific user by their email address"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> getUserByEmail(
            @Parameter(description = "User email", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        
        try {
            UserDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new user
     * 
     * @param userDto the user data
     * @return the created user
     */
    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "User with email already exists",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User data", required = true)
            @Valid @RequestBody UserDto userDto) {
        
        try {
            UserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update user
     * 
     * @param id the user ID
     * @param userDto the updated user data
     * @return the updated user
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update user",
        description = "Updates an existing user's information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true)
            @Valid @RequestBody UserDto userDto) {
        
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete user
     * 
     * @param id the user ID
     * @return deletion confirmation
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete user",
        description = "Deletes a user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User deleted successfully",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search users by name
     * 
     * @param firstName first name to search for
     * @param lastName last name to search for
     * @return list of matching users
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search users by name",
        description = "Searches for users by first name and/or last name"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<UserDto>> searchUsers(
            @Parameter(description = "First name to search for", example = "John")
            @RequestParam(required = false) String firstName,
            @Parameter(description = "Last name to search for", example = "Doe")
            @RequestParam(required = false) String lastName) {
        
        try {
            List<UserDto> users = userService.searchUsersByName(firstName, lastName);
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Search users by email pattern
     * 
     * @param emailPattern email pattern to search for
     * @return list of matching users
     */
    @GetMapping("/search/email")
    @Operation(
        summary = "Search users by email pattern",
        description = "Searches for users by email pattern"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<UserDto>> searchUsersByEmail(
            @Parameter(description = "Email pattern to search for", example = "john")
            @RequestParam String emailPattern) {
        
        try {
            List<UserDto> users = userService.searchUsersByEmail(emailPattern);
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get users created after a specific date
     * 
     * @param date the date to filter by
     * @return list of users created after the date
     */
    @GetMapping("/created-after")
    @Operation(
        summary = "Get users created after date",
        description = "Retrieves users created after a specific date"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<UserDto>> getUsersCreatedAfter(
            @Parameter(description = "Date to filter by", example = "2024-01-01T00:00:00")
            @RequestParam LocalDateTime date) {
        
        try {
            List<UserDto> users = userService.getUsersCreatedAfter(date);
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get users with active cart
     * 
     * @return list of users with active cart
     */
    @GetMapping("/with-active-cart")
    @Operation(
        summary = "Get users with active cart",
        description = "Retrieves users who have items in their shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<UserDto>> getUsersWithActiveCart() {
        
        try {
            List<UserDto> users = userService.getUsersWithActiveCart();
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get users with wishlist
     * 
     * @return list of users with wishlist
     */
    @GetMapping("/with-wishlist")
    @Operation(
        summary = "Get users with wishlist",
        description = "Retrieves users who have items in their wishlist"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<UserDto>> getUsersWithWishlist() {
        
        try {
            List<UserDto> users = userService.getUsersWithWishlist();
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Activate user account
     * 
     * @param id the user ID
     * @return the activated user
     */
    @PutMapping("/{id}/activate")
    @Operation(
        summary = "Activate user account",
        description = "Activates a user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User activated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> activateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        
        try {
            UserDto activatedUser = userService.activateUser(id);
            return ResponseEntity.ok(activatedUser);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate user account
     * 
     * @param id the user ID
     * @return the deactivated user
     */
    @PutMapping("/{id}/deactivate")
    @Operation(
        summary = "Deactivate user account",
        description = "Deactivates a user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User deactivated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> deactivateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        
        try {
            UserDto deactivatedUser = userService.deactivateUser(id);
            return ResponseEntity.ok(deactivatedUser);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get total user count
     * 
     * @return total number of users
     */
    @GetMapping("/count")
    @Operation(
        summary = "Get total user count",
        description = "Retrieves the total number of users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Count retrieved successfully",
            content = @Content(schema = @Schema(implementation = Long.class))
        )
    })
    public ResponseEntity<Long> getTotalUserCount() {
        
        try {
            long count = userService.getTotalUserCount();
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            return ResponseEntity.ok(0L);
        }
    }
} 