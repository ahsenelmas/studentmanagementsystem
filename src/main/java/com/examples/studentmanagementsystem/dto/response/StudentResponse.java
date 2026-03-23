package com.examples.studentmanagementsystem.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentResponse {

    private Long id;
    private String studentNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phone;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // getters & setters
}