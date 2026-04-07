package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.LoginRequest;
import com.examples.studentmanagementsystem.dto.request.RegisterRequest;
import com.examples.studentmanagementsystem.dto.response.AuthResponse;
import com.examples.studentmanagementsystem.dto.response.StudentOptionResponse;
import com.examples.studentmanagementsystem.entity.Role;
import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.entity.User;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.StudentRepository;
import com.examples.studentmanagementsystem.repository.UserRepository;
import com.examples.studentmanagementsystem.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Authentication API", description = "User authentication and authorization")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                          StudentRepository studentRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (request.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getUser() != null) {
            throw new DuplicateResourceException("This student already has an account");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .student(student)
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    @Operation(summary = "Get student options for registration")
    @GetMapping("/student-options")
    public List<StudentOptionResponse> getStudentOptions() {
        return studentRepository.findAll()
                .stream()
                .filter(student -> student.getUser() == null)
                .map(student -> new StudentOptionResponse(
                        student.getId(),
                        student.getStudentNumber(),
                        student.getFirstName() + " " + student.getLastName()
                ))
                .toList();
    }

    @Operation(summary = "Login and get JWT token")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(request.getUsername());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole()
        );
    }
}