package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.entity.UserDetail;
import com.example.Web_sale_app.enums.Role;
import com.example.Web_sale_app.service.MyUsersDetailService;
import com.example.Web_sale_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/All")
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }

    @PostMapping("/setEnabled/username/enabled")
    public ResponseEntity<?> setEnabled(String username, boolean enabled){
        boolean result = userService.setEnabled(username, enabled);
        if (result) {
            return ResponseEntity.ok("Enabled user successfully!");
        } else {
            return ResponseEntity.status(400).body(result);
        }

    }

    @PostMapping("/setRole/username/role")
    public ResponseEntity<?> setRole(String username, Role role){
        boolean result = userService.setRole(username, role);
        if (result) {
            return ResponseEntity.ok("Role set successfully!");
        } else {
            return ResponseEntity.status(400).body(result);
        }
    }
}
