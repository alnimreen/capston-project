
package com.example.collabcode.config;

import com.example.collabcode.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/","/api/**","/api/rooms", "/api/auth/**", "/api/login", "/api/register","/api/createRoom").permitAll()
                        .requestMatchers("/api/rooms").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/websocket/**").permitAll() // Allow access to WebSocket endpoint
                        .requestMatchers( "/oauth2/**").permitAll()
                        .requestMatchers("/api/files/**").permitAll()
                        .anyRequest().authenticated()
                )
                           .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/auth/login/success", true)  // Ensure this matches your success endpoint
                        .failureUrl("/api/auth/loginFailure") // URL to handle failures
                            );

        System.out.println("Failure URL configured: /api/auth/loginFailure");

        return http.build();
    }
}
