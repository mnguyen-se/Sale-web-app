package com.example.Web_sale_app.config;

import com.example.Web_sale_app.service.impl.OAuth2UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private OAuth2UserServiceImpl oAuth2UserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));// domain frontend
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // nếu gửi cookie hoặc header Authorization

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 👈 API JWT chuẩn
                .authorizeHttpRequests(auth -> auth
                        // Swagger và OpenAPI endpoints
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**", "/swagger-ui/index.html").permitAll()

                        // Public catalog & cart
                        .requestMatchers("/api/catalog/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()

                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        //GEMINI API
                        .requestMatchers("/api/chat").permitAll()

                        // Auth
                        .requestMatchers("/api/auth/register"
                        , "/api/auth/login"
                        ,"/api/auth/confirm").permitAll()

                        // OAuth2 endpoints
                        .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()

                        //Chatbot
                        .requestMatchers("/api/chatbot/chat").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/checkout").permitAll()
                        .requestMatchers("/api/orders/guest/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/me/**").authenticated()
                        // Còn lại cần auth
                        .anyRequest().authenticated()
                )
                // Nếu không dùng httpBasic, có thể bỏ:
                //.httpBasic(Customizer.withDefaults())
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(u -> u.userService(oAuth2UserService)))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cấu hình Authentication Provider với BCrypt encoder
     * Sử dụng builder pattern cho Spring Security 6+
     *
     * @param userDetailsService service để load user details
     * @return DaoAuthenticationProvider được cấu hình
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
