package com.examples.studentmanagementsystem.dto.response;

import java.time.LocalDate;
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

    // getters & setters
}