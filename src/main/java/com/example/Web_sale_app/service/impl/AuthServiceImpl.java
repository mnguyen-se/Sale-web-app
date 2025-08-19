package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.service.AuthService;
import com.example.Web_sale_app.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

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
