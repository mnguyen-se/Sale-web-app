package com.example.Web_sale_app.entity.ReqDTO;

import com.example.Web_sale_app.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReqRegisterDTO {
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Role role;

}
