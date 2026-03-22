package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Enrollment;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.EnrollmentRepository;
import com.examples.studentmanagementsystem.service.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Enrollment createEnrollment(Enrollment enrollment) {
        Long studentId = enrollment.getStudent().getId();
        Long courseId = enrollment.getCourse().getId();

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("Student is already enrolled in this course.");
        }

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
}