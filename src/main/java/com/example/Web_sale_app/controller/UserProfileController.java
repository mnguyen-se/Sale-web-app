package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.enums.Role;
import com.example.Web_sale_app.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userService;
    
    // GET all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            return user.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.findByEmail(email);
            return user.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // POST create new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // Basic validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PUT update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // Check if user exists
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Set the ID to ensure we're updating the correct user
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PATCH update user enabled status
    @PatchMapping("/{id}/enabled")
    public ResponseEntity<String> updateUserEnabledStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            // First get the user by ID to get the username
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            boolean success = userService.setEnabled(user.get().getUsername(), enabled);
            if (success) {
                return ResponseEntity.ok("User enabled status updated successfully");
            }
            return ResponseEntity.badRequest().body("Failed to update user enabled status");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PATCH update user role
    @PatchMapping("/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        try {
            // First get the user by ID to get the username
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            boolean success = userService.setRole(user.get().getUsername(), role);
            if (success) {
                return ResponseEntity.ok("User role updated successfully");
            }
            return ResponseEntity.badRequest().body("Failed to update user role");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
 // NEW: Safe delete endpoint (optional - for explicit safe deletion)
    @DeleteMapping("/{id}/safe")
    public ResponseEntity<String> safeDeleteUser(@PathVariable Long id) {
        try {
            // This assumes you expose safeDeleteUser in the service interface
            boolean success = userService.deleteUser(id); // or safeDeleteUser if exposed
            if (success) {
                return ResponseEntity.ok("User safely deleted with complete cleanup");
            }
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Safe delete failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error during safe user deletion");
        }
    }
}
