package com.tiaadeals.server.repository;

import com.tiaadeals.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * 
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by first name (case-insensitive)
     * 
     * @param firstName the first name to search for
     * @return list of users with matching first name
     */
    List<User> findByFirstNameIgnoreCase(String firstName);

    /**
     * Find users by last name (case-insensitive)
     * 
     * @param lastName the last name to search for
     * @return list of users with matching last name
     */
    List<User> findByLastNameIgnoreCase(String lastName);

    /**
     * Find users by first name or last name (case-insensitive)
     * 
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return list of users with matching first or last name
     */
    List<User> findByFirstNameIgnoreCaseOrLastNameIgnoreCase(String firstName, String lastName);

    /**
     * Find users created after a specific date
     * 
     * @param date the date to filter by
     * @return list of users created after the specified date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find users with active cart items
     * 
     * @return list of users who have items in their cart
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.cartItems ci WHERE ci.quantity > 0")
    List<User> findUsersWithActiveCart();

    /**
     * Find users with wishlist items
     * 
     * @return list of users who have items in their wishlist
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.wishlistItems wi")
    List<User> findUsersWithWishlist();

    /**
     * Count total number of users
     * 
     * @return total user count
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();

    /**
     * Find users by email pattern (for search functionality)
     * 
     * @param emailPattern the email pattern to search for (supports % wildcards)
     * @return list of users matching the email pattern
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:emailPattern%")
    List<User> findByEmailPattern(@Param("emailPattern") String emailPattern);

    /**
     * Find users with pagination and sorting
     * 
     * @param pageable pagination and sorting parameters
     * @return page of users
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    org.springframework.data.domain.Page<User> findAllUsersWithPagination(org.springframework.data.domain.Pageable pageable);
} 