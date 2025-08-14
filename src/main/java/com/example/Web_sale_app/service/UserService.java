package com.example.Web_sale_app.service;

import com.example.Web_sale_app.config.SecurityConfig;
import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SecurityConfig config;

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


    public String verify(ReqLoginDTO reqLoginDTO) {

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(),
                        reqLoginDTO.getPassword()));
        System.out.println(reqLoginDTO.getPassword());
        if(authentication.isAuthenticated())
            return jwtService.generateToken(reqLoginDTO.getUsername());
        else
            return "Fail";
    }

}
