package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.LoginRequest;
import com.examples.studentmanagementsystem.dto.request.RegisterRequest;
import com.examples.studentmanagementsystem.entity.Role;
import com.examples.studentmanagementsystem.entity.User;
import com.examples.studentmanagementsystem.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldCreateUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("student1");
        request.setEmail("student1@test.com");
        request.setPassword("pass123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() throws Exception {
        User user = User.builder()
                .username("student1")
                .email("student1@test.com")
                .password(passwordEncoder.encode("pass123"))
                .role(Role.STUDENT)
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("student1");
        request.setPassword("pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void register_ShouldFail_WhenUsernameAlreadyExists() throws Exception {
        User existingUser = User.builder()
                .username("student1")
                .email("existing@test.com")
                .password(passwordEncoder.encode("pass123"))
                .role(Role.STUDENT)
                .build();

        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("student1");
        request.setEmail("newmail@test.com");
        request.setPassword("pass123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Username already exists")));
    }

    @Test
    void register_ShouldFail_WhenEmailAlreadyExists() throws Exception {
        User existingUser = User.builder()
                .username("existinguser")
                .email("student1@test.com")
                .password(passwordEncoder.encode("pass123"))
                .role(Role.STUDENT)
                .build();

        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("student1");
        request.setEmail("student1@test.com");
        request.setPassword("pass123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
    }
}