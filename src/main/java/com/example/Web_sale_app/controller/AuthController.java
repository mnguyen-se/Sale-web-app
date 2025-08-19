package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.service.AuthService;
import com.example.Web_sale_app.service.BlacklistService;
import com.example.Web_sale_app.service.UserService;
import com.example.Web_sale_app.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final BlacklistService blacklistService;
    private final UserService userService;
    private final AuthService authService;
    public AuthController(BlacklistService blacklistService, UserService userService, AuthService authService) {
        this.blacklistService = blacklistService;
        this.userService = userService;
        this.authService = authService;
    }
    @PostMapping("/login")
    public String login(@RequestBody ReqLoginDTO reqLoginDTO){
        return authService.verify(reqLoginDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.addToken(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }


}
