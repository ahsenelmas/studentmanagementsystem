package com.examples.studentmanagementsystem.dto.request;

import com.examples.studentmanagementsystem.entity.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EnrollmentRequest {

    @NotNull
    private LocalDate enrollmentDate;

    @NotNull
    private EnrollmentStatus status;

    @NotNull
    private Long studentId;

    @NotNull
    private Long courseId;
}