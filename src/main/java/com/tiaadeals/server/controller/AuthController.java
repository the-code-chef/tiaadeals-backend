package com.tiaadeals.server.controller;

import com.tiaadeals.server.dto.AuthResponseDto;
import com.tiaadeals.server.dto.LoginRequestDto;
import com.tiaadeals.server.dto.UserDto;
import com.tiaadeals.server.entity.User;
import com.tiaadeals.server.security.JwtTokenProvider;
import com.tiaadeals.server.service.UserService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * 
     * @param userDto the user registration data
     * @return the created user with authentication response
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information. " +
                     "**Important Notes:** " +
                     "• Password must be 8-128 characters with at least one uppercase, lowercase, number, and special character " +
                     "• Username must be 3-50 characters, alphanumeric + underscore only " +
                     "• Email must be unique and valid format " +
                     "• Do NOT include system fields like 'id', 'fullName', 'createdAt', 'updatedAt' in the request " +
                     "• Only send the required fields and optional fields you want to set",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = {
                    @ExampleObject(
                        name = "Minimal Registration",
                        summary = "Register with only required fields",
                        description = "Basic registration with minimum required information",
                        value = """
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "username": "johndoe123",
                          "password": "SecurePass123!"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Complete Registration",
                        summary = "Register with all fields",
                        description = "Registration with all available fields including optional ones",
                        value = """
                        {
                          "firstName": "Jane",
                          "lastName": "Smith",
                          "email": "jane.smith@example.com",
                          "username": "jane_smith",
                          "password": "MySecurePass123!",
                          "phoneNumber": "+1234567890",
                          "address": "123 Main Street, City, State 12345"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Invalid Example",
                        summary = "What NOT to do",
                        description = "This example shows common mistakes to avoid",
                        value = """
                        {
                          "fullName": "Wrong Field",
                          "id": 1,
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "username": "johndoe123",
                          "password": "weak",
                          "createdAt": "2025-07-19T08:48:46.630Z",
                          "updatedAt": "2025-07-19T08:48:46.630Z"
                        }
                        """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "User registered successfully",
            content = @Content(
                schema = @Schema(implementation = AuthResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "Success Response",
                        summary = "Successful registration response",
                        value = """
                        {
                          "success": true,
                          "message": "User registered successfully",
                          "token": "eyJhbGciOiJIUzI1NiJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                          "tokenType": "Bearer",
                          "expiresIn": 86400,
                          "user": {
                            "id": 1,
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@example.com",
                            "username": "johndoe123",
                            "isActive": true,
                            "role": "USER",
                            "createdAt": "2025-07-19T09:18:49",
                            "updatedAt": "2025-07-19T09:18:49"
                          }
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data - Validation errors",
            content = @Content(
                schema = @Schema(implementation = String.class),
                examples = {
                    @ExampleObject(
                        name = "Password Too Weak",
                        summary = "Password doesn't meet complexity requirements",
                        value = """
                        {
                          "success": false,
                          "message": "Registration failed: Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Invalid Username",
                        summary = "Username format is invalid",
                        value = """
                        {
                          "success": false,
                          "message": "Registration failed: Username can only contain letters, numbers, and underscores"
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "User with email already exists",
            content = @Content(
                schema = @Schema(implementation = String.class),
                examples = {
                    @ExampleObject(
                        name = "Email Already Exists",
                        summary = "Email is already registered",
                        value = """
                        {
                          "success": false,
                          "message": "Registration failed: User with this email already exists"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<AuthResponseDto> register(
            @Parameter(description = "User registration data", required = true)
            @Valid @RequestBody UserDto userDto) {
        
        try {
            UserDto createdUser = userService.createUser(userDto);
            
            // Get the created user entity for token generation
            User user = userService.getUserEntityByEmail(createdUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve created user"));
            
            // Generate JWT token and refresh token
            String token = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            AuthResponseDto response = AuthResponseDto.success(
                "User registered successfully", 
                token, 
                refreshToken, 
                jwtTokenProvider.getJwtExpirationMs() / 1000, // Convert to seconds
                createdUser
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            AuthResponseDto errorResponse = AuthResponseDto.failure("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Login user
     * 
     * @param loginRequest the login credentials
     * @return authentication response with tokens
     */
    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates user with email and password"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<AuthResponseDto> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequestDto loginRequest) {
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userService.getUserEntityByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
            
            // Generate JWT token and refresh token
            String token = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            AuthResponseDto response = AuthResponseDto.success(
                "Login successful", 
                token, 
                refreshToken, 
                jwtTokenProvider.getJwtExpirationMs() / 1000, // Convert to seconds
                userService.getUserByEmail(loginRequest.getEmail())
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            AuthResponseDto errorResponse = AuthResponseDto.failure("Login failed: Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Refresh authentication token
     * 
     * @param refreshToken the refresh token
     * @return new authentication tokens
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh authentication token",
        description = "Generates new access token using refresh token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid refresh token",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<AuthResponseDto> refreshToken(
            @Parameter(description = "Refresh token", required = true)
            @RequestParam String refreshToken) {
        
        try {
            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            // Extract user information from refresh token
            String email = jwtTokenProvider.extractUsername(refreshToken);
            User user = userService.getUserEntityByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate new tokens
            String newToken = jwtTokenProvider.generateToken(user);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            AuthResponseDto response = AuthResponseDto.success(
                "Token refreshed successfully", 
                newToken, 
                newRefreshToken, 
                jwtTokenProvider.getJwtExpirationMs() / 1000, // Convert to seconds
                null // User info not needed for refresh
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            AuthResponseDto errorResponse = AuthResponseDto.failure("Token refresh failed: Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Logout user
     * 
     * @param token the authentication token to invalidate
     * @return logout confirmation
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Invalidates the current authentication token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Logout successful",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<String> logout(
            @Parameter(description = "Authentication token", required = true)
            @RequestHeader("Authorization") String token) {
        
        // TODO: Implement token blacklisting for logout
        // For now, just return success - client should discard the token
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Get current user profile
     * 
     * @param token the authentication token
     * @return current user information
     */
    @GetMapping("/profile")
    @Operation(
        summary = "Get current user profile",
        description = "Retrieves the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> getProfile(
            @Parameter(description = "Authentication token", required = true)
            @RequestHeader("Authorization") String token) {
        
        try {
            // Extract user information from JWT token
            String jwt = token.replace("Bearer ", "");
            String email = jwtTokenProvider.extractUsername(jwt);
            
            UserDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Update user profile
     * 
     * @param token the authentication token
     * @param userDto the updated user data
     * @return updated user information
     */
    @PutMapping("/profile")
    @Operation(
        summary = "Update user profile",
        description = "Updates the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<UserDto> updateProfile(
            @Parameter(description = "Authentication token", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Updated user data", required = true)
            @Valid @RequestBody UserDto userDto) {
        
        try {
            // Extract user ID from JWT token
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.extractUserId(jwt);
            
            UserDto updatedUser = userService.updateUser(userId, userDto);
            return ResponseEntity.ok(updatedUser);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Change user password
     * 
     * @param token the authentication token
     * @param newPassword the new password
     * @return password change confirmation
     */
    @PutMapping("/change-password")
    @Operation(
        summary = "Change user password",
        description = "Changes the password of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Password changed successfully",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid password",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<String> changePassword(
            @Parameter(description = "Authentication token", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "New password", required = true)
            @RequestParam String newPassword) {
        
        try {
            // Extract user ID from JWT token
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.extractUserId(jwt);
            
            userService.changePassword(userId, newPassword);
            return ResponseEntity.ok("Password changed successfully");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password change failed: " + e.getMessage());
        }
    }
} 