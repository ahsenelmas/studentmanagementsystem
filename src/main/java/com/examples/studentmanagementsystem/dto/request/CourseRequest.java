package com.examples.studentmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {

    @NotBlank
    private String courseCode;

    @NotBlank
    private String courseName;

    @NotNull
    private Integer credit;

    private String description;
}