package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.entity.UserDetail;
import com.example.Web_sale_app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUsersDetailService implements UserDetailsService {

    private UserRepository userRepository;

    public MyUsersDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("user not found");
        }
        System.out.println(user);
        return new UserDetail(user);
    }

}
