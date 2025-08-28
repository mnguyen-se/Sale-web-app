package com.example.Web_sale_app.dto.Res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserResponse {
    private String username;
    private String name;
    private String email;
}
