package com.metahrms.employee_management.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = { 
        "/auth/login",
        "/auth/logout",
        "/auth/forgot-password",
        "/api/auth/login",
        "/api/auth/logout",
        "/api/auth/forgot-password"
    };
    private final String[] SWAGGER_ENDPOINTS = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/actuator/health"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/locations/**").authenticated()
                .anyRequest().authenticated()
        );

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS256");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ⭐ THAY ĐỔI: Cho phép tất cả origins trong development
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Hoặc nếu muốn cụ thể:
        // configuration.setAllowedOrigins(List.of(
        //     "http://localhost:5173",     // Web
        //     "http://34.126.173.70",      // Production
        //     "http://192.168.0.103:8081"  // React Native (Expo)
        // ));
        
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Hoặc đơn giản hơn - cho phép tất cả headers:
        configuration.setAllowedHeaders(List.of("*"));
        
        configuration.setAllowCredentials(true);
        
        // ✅ THÊM X-User-Id VÀO DANH SÁCH ALLOWED HEADERS
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-User-Id",                           // ← THÊM DÒNG NÀY
            "Access-Control-Allow-Credentials",
            "X-Requested-With",
            "Accept"
        ));
        
        
        // ✅ NÊN THÊM: Expose headers nếu frontend cần đọc response headers
        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-User-Id"
        ));
        
        // ✅ NÊN THÊM: Cache preflight request
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Filter jwtAuthenticationFilter() {
        return (request, response, chain) -> {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            System.out.println("🔍 Processing: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
            String requestPath = httpRequest.getRequestURI();

            // Priority 1: Get token from Authorization header (for mobile)
            String token = getTokenFromAuthorizationHeader(httpRequest);

            // // First, try to get token from cookie
            // String token = getTokenFromCookie(httpRequest);

            // Priority 2: Get token from cookie (for web)
            if (token == null) {
                token = getTokenFromCookie(httpRequest);
            }

            if (token != null) {
                try {
                    Jwt jwt = jwtDecoder().decode(token);

                    String id = jwt.getClaimAsString("id");
                    String username = jwt.getClaimAsString("username");
                    String email = jwt.getClaimAsString("email");
                    String role = jwt.getClaimAsString("role");

                    // ⭐ THÊM: Log để debug
                    System.out.println("✅ Authenticated user: " + username + " (ID: " + id + ")");

                    Map<String, Object> userInfo = Map.of(
                            "id", id,
                            "username", username,
                            "email", email,
                            "role", role
                    );

                    request.setAttribute("user", userInfo);
                    
                    String springRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                    UserDetails userDetails = User.builder()
                            .username(username)
                            .password("")
                            .authorities(springRole)
                            .build();

                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } catch (Exception e) {
                    // ⭐ SỬA: Log chi tiết hơn
                    System.err.println("❌ Invalid JWT: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ No token found in request");
            }

            chain.doFilter(request, response);
        };
    }


    private String getTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
