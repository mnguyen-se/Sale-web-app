package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.enums.Role;
import com.example.Web_sale_app.repository.UserRepository;
import com.example.Web_sale_app.repository.ConfirmationTokenRepository;
import com.example.Web_sale_app.service.UserProfileService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    
    public UserProfileServiceImpl(UserRepository userRepository, 
                                 ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    // Create
    @Override
    public User createUser(User user) {
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }

    // Read
    @Override
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update
    @Override
    public User updateUser(User user) {
        user.setUpdatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public boolean setEnabled(String username, boolean enabled){
        User user = userRepository.findByUsername(username);
        if (user == null){
            return false;
        }
        
        // If disabling user, cleanup confirmation tokens
        if (!enabled) {
            cleanupUserRelatedData(user.getId());
        }
        
        user.setEnabled(enabled);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean setRole(String username, Role role){
        User user = userRepository.findByUsername(username);
        if (user == null){
            return false;
        }
        user.setRole(role);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return true;
    }

    // Delete - with manual cleanup
    @Override
    public boolean deleteUser(Long id) {
        try {
            if (userRepository.existsById(id)) {
                // Clean up related records first
                cleanupUserRelatedData(id);
                
                // Then delete the user
                userRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete user due to data constraints: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                // Clean up related records first
                cleanupUserRelatedData(user.getId());
                
                // Then delete the user
                userRepository.delete(user);
                return true;
            }
            return false;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete user due to data constraints: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Clean up all user-related data before deletion or when disabling user
     * This method handles the manual cleanup to avoid foreign key constraint violations
     */
    private void cleanupUserRelatedData(Long userId) {
        try {
            // 1. Clean up confirmation tokens
            confirmationTokenRepository.deleteByUserId(userId);
            
            // 2. Add other cleanup operations as needed:
            // If you have other entities with user_id foreign key, add cleanup here:
            
            // Example for orders (if exists):
            // orderRepository.deleteByUserId(userId);
            
            // Example for cart items (if exists):
            // cartItemRepository.deleteByUserId(userId);
            
            // Example for notifications (if exists):
            // notificationRepository.deleteByUserId(userId);
            
            // Example for reviews (if exists):
            // reviewRepository.deleteByUserId(userId);
            
            System.out.println("Successfully cleaned up user-related data for user ID: " + userId);
            
        } catch (Exception e) {
            System.err.println("Error cleaning up user-related data for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to cleanup user-related data", e);
        }
    }
    
    /**
     * Safe delete method - checks for related data before deletion
     */
    public boolean safeDeleteUser(Long userId) {
        try {
            // Check if user exists
            if (!userRepository.existsById(userId)) {
                return false;
            }
            
            // Get user info for logging
            User user = userRepository.findById(userId).orElse(null);
            String username = user != null ? user.getUsername() : "Unknown";
            
            // Count related records before deletion
            long tokenCount = confirmationTokenRepository.countByUserId(userId);
            System.out.println("User " + username + " has " + tokenCount + " confirmation tokens to be deleted");
            
            // Perform cleanup and deletion
            cleanupUserRelatedData(userId);
            userRepository.deleteById(userId);
            
            System.out.println("Successfully deleted user: " + username + " (ID: " + userId + ")");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error in safe delete for user ID " + userId + ": " + e.getMessage());
            throw new RuntimeException("Safe delete failed", e);
        }
    }
}
