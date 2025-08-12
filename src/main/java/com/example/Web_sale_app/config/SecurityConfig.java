package com.example.Web_sale_app.config;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Cho phép swagger & docs
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Cho phép public catalog nếu muốn
                        .requestMatchers("/api/catalog/**").permitAll()
                        // còn lại cần auth
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
