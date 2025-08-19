package com.example.Web_sale_app.service;

import com.example.Web_sale_app.config.SecurityConfig;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final SecurityConfig config;

    public UserService(UserRepository userRepository, SecurityConfig config) {
        this.userRepository = userRepository;
        this.config = config;
    }

    public User register(ReqRegisterDTO req){
        req.setPassword(config.bCryptPasswordEncoder().encode(req.getPassword()));
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole("customer");
        user.setCreatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

}
