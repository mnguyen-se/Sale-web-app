package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.entity.UserDetail;
import com.example.Web_sale_app.service.MyUsersDetailService;
import com.example.Web_sale_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody ReqLoginDTO reqLoginDTO){
        return userService.verify(reqLoginDTO);
    }

    @GetMapping("/All")
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }

}
