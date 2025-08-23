package com.example.Web_sale_app.service;

import com.example.Web_sale_app.config.SecurityConfig;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.enums.Role;
import com.example.Web_sale_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }


    public boolean setEnabled(String username, boolean enabled){
        User user = userRepository.findByUsername(username);
        if (user == null){
            return false;
        }
        user.setEnabled(enabled);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return true;
    }

    public boolean setRole(String username, Role role){
        User user = userRepository.findByUsername(username);
        if (user == null){
            return false;
        }
        user.setRole(role);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return true;
    }
}
