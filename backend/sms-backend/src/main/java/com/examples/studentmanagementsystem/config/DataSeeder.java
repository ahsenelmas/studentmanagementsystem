package com.examples.studentmanagementsystem.config;

import com.examples.studentmanagementsystem.entity.Role;
import com.examples.studentmanagementsystem.entity.User;
import com.examples.studentmanagementsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@sms.com")
                    .password(passwordEncoder.encode("Admin123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}