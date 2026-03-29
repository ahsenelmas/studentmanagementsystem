package com.examples.studentmanagementsystem.service;

import com.examples.studentmanagementsystem.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {

    Enrollment createEnrollment(Enrollment enrollment);

    Enrollment getEnrollmentById(Long id);

    List<Enrollment> getAllEnrollments();

    Enrollment updateEnrollment(Long id, Enrollment enrollment);

    void deleteEnrollment(Long id);

    List<Enrollment> getEnrollmentsByStudentId(Long studentId);

    List<Enrollment> getEnrollmentsByCourseId(Long courseId);
}