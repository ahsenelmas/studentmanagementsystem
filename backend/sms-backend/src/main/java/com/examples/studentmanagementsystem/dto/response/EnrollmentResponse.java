package com.examples.studentmanagementsystem.dto.response;

import com.examples.studentmanagementsystem.entity.EnrollmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EnrollmentResponse {

    private Long id;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;

    private Long studentId;
    private String studentNumber;
    private String studentName;

    private Long courseId;
    private String courseCode;
    private String courseName;
}