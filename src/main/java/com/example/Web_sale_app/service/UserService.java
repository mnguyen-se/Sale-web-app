package com.example.Web_sale_app.service;

import com.example.Web_sale_app.config.SecurityConfig;
import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private SecurityConfig config;

    public User register(User user){
        user.setPassword(config.bCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }


    public String verify(ReqLoginDTO reqLoginDTO) {

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(),
                        reqLoginDTO.getPassword()));
        System.out.println(reqLoginDTO.getPassword());
        if(authentication.isAuthenticated())
            return "Success";
        else
            return "Fail";
    }
}
