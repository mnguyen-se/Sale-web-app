package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserProfileService {
    // Create
    User createUser(User user);
    
    // Read
    List<User> findAllUsers();
    Optional<User> findById(Long id);
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Update
    User updateUser(User user);
    boolean setEnabled(String username, boolean enabled);
    boolean setRole(String username, Role role);
    
    // Delete
    boolean deleteUser(Long id);
    boolean deleteByUsername(String username);
}
