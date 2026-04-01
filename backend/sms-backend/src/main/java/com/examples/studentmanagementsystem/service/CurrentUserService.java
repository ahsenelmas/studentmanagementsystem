package com.examples.studentmanagementsystem.service;

import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.entity.User;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found.");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public Student getCurrentStudent() {
        User user = getCurrentUser();

        if (user.getStudent() == null) {
            throw new ResourceNotFoundException("No student profile is linked to this user.");
        }

        return user.getStudent();
    }
}