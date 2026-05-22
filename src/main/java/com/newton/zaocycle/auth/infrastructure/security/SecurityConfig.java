package com.newton.zaocycle.auth.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public webhook / callback endpoints
                        .requestMatchers("/api/v1/ussd/**").permitAll()
                        .requestMatchers(POST, "/api/v1/sms/inbound").permitAll()
                        .requestMatchers("/api/v1/payments/payhero/**").permitAll()
                        // Public certificate verification
                        .requestMatchers("/api/v1/certificates/verify/**").permitAll()
                        // Public product catalogue
                        .requestMatchers(GET, "/api/v1/products/**").permitAll()
                        // Public farmer registration (USSD flow)
                        .requestMatchers("/api/v1/farmers/**").permitAll()
                        .requestMatchers("/api/v1/chemicals/**").permitAll()
                        .requestMatchers("/api/v1/pesticide-applications/**").permitAll()
                        // Docs & health
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(GET, "/actuator/health").permitAll()
                        // Auth — only the unauthenticated endpoints are public; /me requires a token
                        .requestMatchers(POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(POST, "/api/v1/auth/buyer/register").permitAll()
                        .requestMatchers(POST, "/api/v1/auth/rider/register").permitAll()
                        .requestMatchers(POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers(POST, "/api/v1/auth/logout").permitAll()
                        // Role-protected routes
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("COOP_MANAGER", "ADMIN")
                        .requestMatchers("/api/v1/rider/**").hasRole("RIDER")
                        .requestMatchers("/api/v1/farmer/**").hasRole("FARMER")
                        .requestMatchers("/api/v1/buyer/**").hasRole("BUYER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "https://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException(username);
        };
    }
}
