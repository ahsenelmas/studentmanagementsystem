package com.examples.studentmanagementsystem.mapper;

import com.examples.studentmanagementsystem.dto.response.EnrollmentResponse;
import com.examples.studentmanagementsystem.entity.Enrollment;

public class EnrollmentMapper {

    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();

        response.setId(enrollment.getId());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        response.setStatus(enrollment.getStatus());

        if (enrollment.getStudent() != null) {
            response.setStudentId(enrollment.getStudent().getId());
            response.setStudentNumber(enrollment.getStudent().getStudentNumber());
            response.setStudentName(
                    enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName()
            );
        }

        if (enrollment.getCourse() != null) {
            response.setCourseId(enrollment.getCourse().getId());
            response.setCourseCode(enrollment.getCourse().getCourseCode());
            response.setCourseName(enrollment.getCourse().getCourseName());
        }

        return response;
    }
}