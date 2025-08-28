package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.Req.UpdateCurrentUserProfileRequest;
import com.example.Web_sale_app.dto.Res.CurrentUserResponse;

import java.util.Optional;

public interface CurrentUserProfileService {
    
   
    Optional<CurrentUserResponse> getCurrentUserProfile();
    
  
    String updateCurrentUserProfile(UpdateCurrentUserProfileRequest request);
    
  
    boolean changePassword(String currentPassword, String newPassword);

    boolean verifyCurrentPassword(String password);
}

