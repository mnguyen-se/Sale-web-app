package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.service.AuthService;
import com.example.Web_sale_app.service.BlacklistService;
import com.example.Web_sale_app.service.UserService;
import com.example.Web_sale_app.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final BlacklistService blacklistService;
    private final AuthService authService;
    public AuthController(BlacklistService blacklistService, AuthService authService) {
        this.blacklistService = blacklistService;
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

    @PostMapping("/register")
    public String register(@RequestBody ReqRegisterDTO req){
        return authService.register(req);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        // logic xác thực token
        boolean success = authService.confirm(token);
        if (success) {
            return ResponseEntity.ok("Account confirmed!");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }
}
