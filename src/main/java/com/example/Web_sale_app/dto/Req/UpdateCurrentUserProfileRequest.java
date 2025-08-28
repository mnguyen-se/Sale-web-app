package com.example.Web_sale_app.dto.Req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCurrentUserProfileRequest {
    private String username;
    private String name;
    private String email;
}
