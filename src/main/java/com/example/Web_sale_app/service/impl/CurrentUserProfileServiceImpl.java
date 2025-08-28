package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.Req.UpdateCurrentUserProfileRequest;
import com.example.Web_sale_app.dto.Res.CurrentUserResponse;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.repository.UserRepository;
import com.example.Web_sale_app.service.CurrentUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Transactional
public class CurrentUserProfileServiceImpl implements CurrentUserProfileService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Optional<CurrentUserResponse> getCurrentUserProfile() {
        String username = getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        
        // Convert User entity to CurrentUserResponse DTO
        CurrentUserResponse response = new CurrentUserResponse();
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        
        return Optional.of(response);
    }
    
       @Override
    public String updateCurrentUserProfile(UpdateCurrentUserProfileRequest request) {
        String currentUsername = getCurrentUsername();
        if (currentUsername == null) {
            throw new RuntimeException("No authenticated user found");
        }
        
        User existingUser = userRepository.findByUsername(currentUsername);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        
        // Update allowed fields (not password, role, etc.)
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            // Check if username already exists
            User existingUsernameUser = userRepository.findByUsername(request.getUsername());
            if (existingUsernameUser != null && !existingUsernameUser.getId().equals(existingUser.getId())) {
                throw new RuntimeException("Username already exists");
            }
            existingUser.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Check if email already exists
            Optional<User> existingEmailUser = userRepository.findByEmail(request.getEmail());
            if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(existingUser.getId())) {
                throw new RuntimeException("Email already exists");
            }
            existingUser.setEmail(request.getEmail());
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            existingUser.setName(request.getName());
        }
        
        existingUser.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(existingUser);
        
        return "Cap nhat thanh cong";
    }
    
    @Override
    public boolean changePassword(String currentPassword, String newPassword) {
        try {
            String currentUsername = getCurrentUsername();
            if (currentUsername == null) {
                return false;
            }
            
            User user = userRepository.findByUsername(currentUsername);
            if (user == null) {
                return false;
            }
            
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return false;
            }
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(OffsetDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean verifyCurrentPassword(String password) {
        try {
            String currentUsername = getCurrentUsername();
            if (currentUsername == null) {
                return false;
            }
            
            User user = userRepository.findByUsername(currentUsername);
            if (user == null) {
                return false;
            }
            
            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
    
    // Helper method to get current authenticated username
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                return authentication.getName();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
