package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.dto.request.CreateUserRequest;
import com.examples.studentmanagementsystem.entity.Role;
import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.entity.User;
import com.examples.studentmanagementsystem.repository.StudentRepository;
import com.examples.studentmanagementsystem.repository.UserRepository;
import com.examples.studentmanagementsystem.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // 🔥 CRITICAL FIX
        if (request.getRole() == Role.STUDENT) {

            if (request.getStudentId() == null) {
                throw new RuntimeException("Student ID is required for STUDENT role");
            }

            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            if (student.getUser() != null) {
                throw new RuntimeException("This student already has an account");
            }

            user.setStudent(student);
        }

        return userRepository.save(user);
    }
}