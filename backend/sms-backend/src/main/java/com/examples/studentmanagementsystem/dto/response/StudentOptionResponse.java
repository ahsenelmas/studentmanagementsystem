package com.examples.studentmanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentOptionResponse {
    private Long id;
    private String studentNumber;
    private String fullName;
}