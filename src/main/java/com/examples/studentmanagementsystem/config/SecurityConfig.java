package com.examples.studentmanagementsystem.config;

import com.examples.studentmanagementsystem.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // STUDENT + ADMIN can read
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/courses/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/schedules/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/enrollments/**").hasAnyRole("ADMIN", "STUDENT")

                        // only ADMIN can modify
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")

                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/courses/**").hasRole("ADMIN")

                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")

                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/enrollments/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/enrollments/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}