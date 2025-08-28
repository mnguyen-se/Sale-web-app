package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.Req.UpdateCurrentUserProfileRequest;
import com.example.Web_sale_app.dto.Res.CurrentUserResponse;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.service.CurrentUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class CurrentUserProfileController {

    @Autowired
    private CurrentUserProfileService currentUserProfileService;

    @GetMapping
    public ResponseEntity<CurrentUserResponse> getCurrentUserProfile() {
        try {
            Optional<CurrentUserResponse> userResponse = currentUserProfileService.getCurrentUserProfile();
            return userResponse.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping
    public ResponseEntity<String> patchCurrentUserProfile(@RequestBody UpdateCurrentUserProfileRequest request) {
        try {
            String message = currentUserProfileService.updateCurrentUserProfile(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // POST change password
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Current password is required");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("New password is required");
            }
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest().body("New password must be at least 6 characters");
            }

            boolean success = currentUserProfileService.changePassword(currentPassword, newPassword);
            if (success) {
                return ResponseEntity.ok("Password changed successfully");
            }
            return ResponseEntity.badRequest().body("Current password is incorrect");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST verify password
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("valid", false));
            }

            boolean isValid = currentUserProfileService.verifyCurrentPassword(password);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
