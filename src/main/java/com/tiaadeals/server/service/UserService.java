package com.tiaadeals.server.service;

import com.tiaadeals.server.dto.UserDto;
import com.tiaadeals.server.entity.User;
import com.tiaadeals.server.exception.ResourceNotFoundException;
import com.tiaadeals.server.exception.UserAlreadyExistsException;
import com.tiaadeals.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for User business logic
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     * 
     * @param userDto the user data
     * @return the created user DTO
     * @throws UserAlreadyExistsException if user with email already exists
     */
    public UserDto createUser(UserDto userDto) {
        // Check if user with email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }

        // Create new user entity
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(userDto.getAddress());
        user.setIsActive(userDto.getIsActive() != null ? userDto.getIsActive() : true);
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * Get user by ID
     * 
     * @param id the user ID
     * @return the user DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    /**
     * Get user by email
     * 
     * @param email the user email
     * @return the user DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToDto(user);
    }

    /**
     * Get user entity by email (for authentication)
     * 
     * @param email the user email
     * @return the user entity
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user
     * 
     * @param id the user ID
     * @param userDto the updated user data
     * @return the updated user DTO
     * @throws ResourceNotFoundException if user not found
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update fields
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getAddress() != null) {
            user.setAddress(userDto.getAddress());
        }
        if (userDto.getIsActive() != null) {
            user.setIsActive(userDto.getIsActive());
        }
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }

        // Update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * Delete user
     * 
     * @param id the user ID
     * @throws ResourceNotFoundException if user not found
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Get all users with pagination
     * 
     * @param pageable pagination parameters
     * @return page of user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAllUsersWithPagination(pageable)
                .map(this::convertToDto);
    }

    /**
     * Get all users
     * 
     * @return list of user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Search users by name
     * 
     * @param firstName first name to search for
     * @param lastName last name to search for
     * @return list of matching user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByName(String firstName, String lastName) {
        return userRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(firstName, lastName)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Search users by email pattern
     * 
     * @param emailPattern email pattern to search for
     * @return list of matching user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByEmail(String emailPattern) {
        return userRepository.findByEmailPattern(emailPattern)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get users created after a specific date
     * 
     * @param date the date to filter by
     * @return list of user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersCreatedAfter(LocalDateTime date) {
        return userRepository.findUsersCreatedAfter(date)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get users with active cart
     * 
     * @return list of user DTOs with active cart
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersWithActiveCart() {
        return userRepository.findUsersWithActiveCart()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get users with wishlist
     * 
     * @return list of user DTOs with wishlist
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersWithWishlist() {
        return userRepository.findUsersWithWishlist()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if user exists by email
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get total user count
     * 
     * @return total number of users
     */
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.countTotalUsers();
    }

    /**
     * Activate user account
     * 
     * @param id the user ID
     * @return the updated user DTO
     * @throws ResourceNotFoundException if user not found
     */
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * Deactivate user account
     * 
     * @param id the user ID
     * @return the updated user DTO
     * @throws ResourceNotFoundException if user not found
     */
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * Change user password
     * 
     * @param id the user ID
     * @param newPassword the new password
     * @return the updated user DTO
     * @throws ResourceNotFoundException if user not found
     */
    public UserDto changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * Convert User entity to UserDto
     * 
     * @param user the user entity
     * @return the user DTO
     */
    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(user.getAddress());
        userDto.setIsActive(user.getIsActive());
        userDto.setRole(user.getRole());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }
} 