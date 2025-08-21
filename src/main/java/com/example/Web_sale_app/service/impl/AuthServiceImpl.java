package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.config.SecurityConfig;
import com.example.Web_sale_app.entity.ConfirmationToken;
import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.repository.ConfirmationTokenRepository;
import com.example.Web_sale_app.repository.UserRepository;
import com.example.Web_sale_app.service.AuthService;
import com.example.Web_sale_app.service.EmailService;
import com.example.Web_sale_app.service.JWTService;
import com.example.Web_sale_app.service.MyUsersDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    private final ConfirmationTokenRepository tokenRepository;
    private final SecurityConfig config;
    private final EmailService emailService;
    private final UserRepository userRepository;
    @Autowired
    private MyUsersDetailService myUsersDetailService;

    public AuthServiceImpl(ConfirmationTokenRepository tokenRepository,
                           SecurityConfig config,
                           EmailService emailService,
                           UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.config = config;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Override
    public String verify(ReqLoginDTO reqLoginDTO) {
        User userAccount = userRepository.findByUsername(reqLoginDTO.getUsername());
        if(userAccount.isEnabled() == false){
            // Sinh token xác nhận
            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setToken(token);
            confirmationToken.setUser(userAccount);
            confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // hết hạn 15p
            tokenRepository.save(confirmationToken);

            // Gửi mail xác nhận
            String link = "http://localhost:8080/api/auth/confirm?token=" + token;
            emailService.sendMail(
                    userAccount.getEmail(),
                    "Xác nhận tài khoản của bạn",
                    "Nhấn vào link sau để kích hoạt: " + link
            );
            return ("Your account is not activated yet! Please check your email to activate your account.");
        }
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        reqLoginDTO.getUsername(),
                        reqLoginDTO.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = myUsersDetailService.loadUserByUsername(reqLoginDTO.getUsername());
            return jwtService.generateToken(userDetails);        } else {
            return "Login failed!";
        }
    }

    @Override
    public String register(ReqRegisterDTO req) {
        // Mã hóa password
        String encodedPassword = config.bCryptPasswordEncoder().encode(req.getPassword());

        // Tạo user mới
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encodedPassword);
        user.setRole("CUSTOMER");
        user.setCreatedAt(OffsetDateTime.now());
        user.setEnabled(false); // mặc định chưa kích hoạt
        userRepository.save(user);

        // Sinh token xác nhận
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setUser(user);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // hết hạn 15p
        tokenRepository.save(confirmationToken);

        // Gửi mail xác nhận
        String link = "http://localhost:8080/api/auth/confirm?token=" + token;
        emailService.sendMail(
                user.getEmail(),
                "Xác nhận tài khoản của bạn",
                "Nhấn vào link sau để kích hoạt: " + link
        );

        return "Đăng ký thành công! Vui lòng check email để xác nhận tài khoản.";
    }

    @Override
    public boolean confirm(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ!"));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }
}
